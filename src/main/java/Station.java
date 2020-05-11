import java.net.DatagramPacket;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;


/*
You may need to add some persistence for file contents and neighbour name-port values
Where the model "queries" the persistent file for information but the MyFileContents class
survives for the entire lifetime of the server
 */
public class Station {
    private static final int DATAGRAM_BYTE_SIZE = 128;
    private static final int MAX_WORDS_PER_LINE = 5;
    private String myName;
    private int myTcpPort;
    private int myUdpPort;
    private ServerSocket serverSocket;

    public Station(String myName, int myTcpPort, int myUdpPort) {
        this.myName = myName;
        this.myTcpPort = myTcpPort;
        this.myUdpPort = myUdpPort;
    }

    public Station(String myName) {
        this(myName, 0, 0);
    }

    public void runServer() {

        try {
            Selector selector = Selector.open();

            DatagramChannel datagramChannel = DatagramChannel.open();
            datagramChannel.configureBlocking(false);
            datagramChannel.bind(new InetSocketAddress("127.0.0.1", myUdpPort));
            datagramChannel.configureBlocking(false);
            byte[] buffer = new byte[DATAGRAM_BYTE_SIZE];
            DatagramPacket datagramPacket = new DatagramPacket(buffer, buffer.length);
            SelectionKey key = datagramChannel.register(selector, SelectionKey.OP_READ);
            key.attach(datagramPacket);

            //TCP Socket Channel
            ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.configureBlocking(false);
            serverSocketChannel.bind(new InetSocketAddress("127.0.0.1", myTcpPort));
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT );
            //Channel registers with a selector with a key type.
            //Selector selects keys which have channels associated with the,
            while (true) {
                int readyCount = selector.select();
                if (readyCount <= 0) continue;
                Set<SelectionKey> readySet = selector.selectedKeys();
                Iterator<SelectionKey> iterator = readySet.iterator();

                while (iterator.hasNext()) {
                    key = iterator.next();//Combines channel with key of selector and keyType
                    iterator.remove();

                    //Each model may only need to send stuff
                    //Give the model access to the datagramChannel
                    StationModel model = new StationModel(datagramChannel, selector);
                    StationView view = new StationView();
                    StationController controller = new StationController(model, view);

                    if (key.isAcceptable()) {
                        SocketChannel socketChannel = serverSocketChannel.accept();
                        socketChannel.configureBlocking(false);
                        int interestOps = SelectionKey.OP_READ;
                        SelectionKey socketKey = socketChannel.register(selector, interestOps);
                        ByteBuffer buf = ByteBuffer.allocate(2048);
                        socketKey.attach(buf);

                        System.out.println("Connection Accepted From:" + socketChannel.getRemoteAddress() + " By: " + socketChannel.getLocalAddress() + "\n");
                    } else if (key.isReadable()) { //How do I know if I read the whole request       !!!!!!!!!!!!!!!!
                        // Currently assume you will read the whole request
                        if (key.channel() instanceof SocketChannel) {
                            SocketChannel client = (SocketChannel) key.channel();


                            ByteBuffer inputBuf = (ByteBuffer) key.attachment();
                            //Read the buffer
                            while (client.read(inputBuf) > 0) ; //position is set to p +1 where there are p bytes read into ByteStrea,

                            System.out.println("Client: " + client.getRemoteAddress() + " sent to " + client.getLocalAddress());
                            String httpRequest = new String(inputBuf.array()).trim();
                            System.out.println(httpRequest);
                            System.out.println();

                            inputBuf.clear(); //Clear ByteBuffer


                            Router router = new Router(controller);
                            String[] httpRequestSplit = httpRequest.split("\n", 2);
                            String httpRequestFirstLine = httpRequestSplit[0];

                            //Think carefully through weird cases

                            //if (httpRequestSplit.length > 1) { //Got the first line of request
                            String viewString = router.route(httpRequestFirstLine, "");
                            byte[] outBytes = viewString.getBytes(StandardCharsets.UTF_8);
                            inputBuf.put(outBytes);
                            //}

                            key.interestOps(SelectionKey.OP_WRITE); //Assuming youve read everything !!!!!!!!!!!!!!!!!!!!!
                        } else if (key.channel() instanceof DatagramChannel) {
                            datagramChannel = (DatagramChannel) key.channel();
                            DatagramPacket inputDatagramPacket = (DatagramPacket) key.attachment();



                            //Receive Datagram data and sender's address
                            ByteBuffer inputDatagramData = ByteBuffer.allocate(DATAGRAM_BYTE_SIZE);
                            InetSocketAddress senderAddress = (InetSocketAddress) datagramChannel.receive(inputDatagramData);

                            System.out.println("Datagram Packet received from: " + senderAddress + " sent to " + datagramChannel.getLocalAddress());

                            //Convert datagram data to string
                            String datagramRequestString = new String(inputDatagramData.array()).trim();

                            //Get first line of datagram
                            String[] datagramRequestSplit = datagramRequestString.split("\n", 2);
                            String datagramRequestHeader = datagramRequestSplit[0];
                            String datagramRequestBody = "";
                            if (datagramRequestSplit.length == 2) {
                                datagramRequestBody = datagramRequestSplit[1].trim();
                            }


                            //Route the datagram to correct response depending based on datagrams first line
                            Router router = new Router(controller);
                            String datagramResponse = router.route(datagramRequestHeader, datagramRequestBody);
                            if (datagramResponse.length() > 0) { //If there is a response send it.
                                byte[] outBytes = datagramResponse.getBytes(StandardCharsets.UTF_8);
                                //Set datagramPacket data response
                                inputDatagramPacket.setData(outBytes);
                                //Set datagramPacket response address
                                inputDatagramPacket.setSocketAddress(senderAddress); //probably not necessary
                                key.interestOps(SelectionKey.OP_WRITE);
                            }
                              ///NOT NECESSARILY!!!!!!!!!!!!!!!!

                        } else {
                            throw new Exception("Channel is not a SocketChannel or a DatagramChannel");
                        }


                    } else if (key.isWritable()) {
                        if (key.channel() instanceof SocketChannel) {
                            SocketChannel client = (SocketChannel) key.channel();
                            ByteBuffer outputBuffer = (ByteBuffer) key.attachment();


                            System.out.println("About to send the following to client: " + client.getRemoteAddress() + " from " + client.getLocalAddress());
                            String httpResponse = new String(outputBuffer.array()).trim();
                            System.out.println(httpResponse);
                            System.out.println();
                            outputBuffer.flip();

                            while (client.write(outputBuffer) > 0) ; //write to client



                            client.close(); //Assuming youve written everything!!!!!!!!!!!!!!!!!!!!!!!!!

                        } else if (key.channel() instanceof DatagramChannel) {

                            DatagramChannel datagramChan = (DatagramChannel) key.channel();
                            DatagramPacket outputDatagramPacket = (DatagramPacket) key.attachment();

                            //get address to send datagram response to
                            SocketAddress destinationAddress = outputDatagramPacket.getSocketAddress();
                            //get data of the datagram response you want to send
                            byte[] datagramOutputBytes = outputDatagramPacket.getData();
                            ByteBuffer datagramOutput = ByteBuffer.wrap(datagramOutputBytes);

                            //Need flip?

                            datagramChan.send(datagramOutput, destinationAddress);

                            //Do we now want to clear Channel?

                            key.interestOps(SelectionKey.OP_READ);

                        }
                    }
                }

            }

        } catch (Exception e) {
            System.out.println("Err: " + e);
            System.exit(1);
        }
    }


    public static void main(String[] args) {
        String fileLocation = "/Users/alexanderfrazis/Desktop/UWAUnits/2020Sem1/CITS3002/NetworksJavaProject/src/main/serverFiles/";
        String serverName = args[0];
        int myTcpPort = Integer.parseInt(args[1]);
        int myUdpPort = Integer.parseInt(args[2]);

        ArrayList<Integer> neighbourPorts = new ArrayList<>();
        for (int i = 3; i < args.length; i ++) {
            if (args[i].matches("\\d+")) {
                neighbourPorts.add(Integer.parseInt(args[i]));
            } else {
                break; //probably hit &
            }
        }

        //Until we get a better idea of where these go
        MyFileContents fileContents = new MyFileContents(fileLocation + "tt-" + serverName, serverName, myTcpPort, myUdpPort, neighbourPorts);
        StationModel.setFileContents(fileContents);

        Station myStation = new Station(serverName, myTcpPort, myUdpPort);
        myStation.runServer();
    }

}




//        try {
//            serverSocket = new ServerSocket(myPort);
//            System.out.println("Now bound to port " + myPort);
//            serverSocket.setSoTimeout(1000000);
//
//
//            System.out.println("About to listen traffic on my port " + myPort);
//            try {
//                Socket socket = serverSocket.accept();
//                System.out.println("Received request at " + myName + " from: " + socket.getPort());
//
//                BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
//                PrintWriter output = new PrintWriter(socket.getOutputStream(), true);
//
//                Router router = new Router();
//                String httpRequestLine;
//
//                String viewString = null;
//                if (input.ready() && (httpRequestLine = input.readLine()) != null) {
//                    viewString = router.route(httpRequestLine);
//                }
//                if (viewString != null) {
//                    output.write(viewString);
//                }
//                output.close();
//                input.close();
//                socket.close();
//            } catch (Exception e) {
//                System.out.println("Err: " + e);
//                System.exit(1);
//            }
//
//        } catch (Exception e) {
//            System.out.println("Err: " + e);
//            System.exit(1);
//        }
