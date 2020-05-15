import java.net.DatagramPacket;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;


/*
You may need to add some persistence for file contents and neighbour name-port values
Where the model "queries" the persistent file for information but the PersistentServerData class
survives for the entire lifetime of the server
 */
public class Station {
    private static final int DATAGRAM_BYTE_SIZE = 10000;
    private static final int MAX_WORDS_PER_LINE = 5;
    private String myName;
    private int myTcpPort;
    private int myUdpPort;
    private ServerSocket serverSocket;
    private HashMap<Integer, StationController> controllerAwaitingResponse;

    private int myPacketNumber;

    public Station(String myName, int myTcpPort, int myUdpPort) {
        this.myName = myName;
        this.myTcpPort = myTcpPort;
        this.myUdpPort = myUdpPort;
        this.myPacketNumber = 0;
        this.controllerAwaitingResponse = new HashMap<>();

    }

    public static void main(String[] args) {
        String fileLocation = "/Users/alexanderfrazis/Desktop/UWAUnits/2020Sem1/CITS3002/NetworksJavaProject/src/main/serverFiles/";
        String serverName = args[0];
        int myTcpPort = Integer.parseInt(args[1]);
        int myUdpPort = Integer.parseInt(args[2]);

        ArrayList<Integer> neighbourPorts = new ArrayList<>();
        for (int i = 3; i < args.length; i++) {
            if (args[i].matches("\\d+")) {
                neighbourPorts.add(Integer.parseInt(args[i]));
            } else {
                break; //probably hit &
            }
        }

        //Until we get a better idea of where these go
        PersistentServerData fileContents = new PersistentServerData(fileLocation + "tt-" + serverName, serverName, myTcpPort, myUdpPort, neighbourPorts);
        StationModel.setFileContents(fileContents);

        Station myStation = new Station(serverName, myTcpPort, myUdpPort);
        myStation.runServer();
    }

    public void incrementPacketCount() {
        myPacketNumber = (myPacketNumber + 1) % 4096;
    }

    public void addControllerAwaitingResponse(Integer packetNo, StationController controller) {
        controllerAwaitingResponse.put(packetNo, controller);
    }

    public void removeControllerAwaitingResponse(Integer packetNo) {
        controllerAwaitingResponse.remove(packetNo);
    }

    public Station(String myName) {
        this(myName, 0, 0);
    }

    public int getPacketCount() {
        return myPacketNumber;
    }

    /*
    1) Any type of model can be waiting on packets
    2) A model can be waiting on multiple packets
    3)
     */
    public void runServer() {

        try {
            Selector selector = Selector.open();

            //We want datagram channel to block for simplicity
            DatagramChannel datagramChannel = DatagramChannel.open();
            datagramChannel.configureBlocking(false);
            datagramChannel.bind(new InetSocketAddress("127.0.0.1", myUdpPort));
//            datagramChannel.configureBlocking(false);
            byte[] buffer = new byte[DATAGRAM_BYTE_SIZE];
            DatagramPacket datagramPacket = new DatagramPacket(buffer, buffer.length);
            SelectionKey datagramChannelKey = datagramChannel.register(selector, SelectionKey.OP_READ);
            datagramChannelKey.attach(datagramPacket);

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
                    SelectionKey key = iterator.next();//Combines channel with key of selector and keyType
                    iterator.remove();

                    //Each model may only need to send stuff
                    //Give the model access to the datagramChannel

                    /*
                    SocketChannelKeys have  ByteBuffers attached
                    DatagramChannelKeys have datagramPackets attached
                     */

                    if (key.channel() instanceof SocketChannel || key.channel() instanceof ServerSocketChannel) {

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
                            SocketChannel client = (SocketChannel) key.channel();


                            ByteBuffer inputBuf = (ByteBuffer) key.attachment();
                            //Read the buffer
                            while (client.read(inputBuf) > 0)
                                ; //position is set to p + 1 where there are p bytes read into ByteStrea,

                            System.out.println("Client: " + client.getRemoteAddress() + " sent to " + client.getLocalAddress());
                            String httpRequest = new String(inputBuf.array()).trim();
                            System.out.println(httpRequest);
                            System.out.println();

                            inputBuf.clear(); //Clear ByteBuffer


                            StationModel httpModel = new StationModel(datagramChannelKey, this, true);
                            StationView httpView = new StationView();
                            //Give controller ther Http Key
                            StationController httpController = new StationController(httpModel, httpView, key, this, true);
                            // controllerWaitingForResponse.


                            Router router = new Router(httpController);
                            String[] httpRequestSplit = httpRequest.split("\n", 2);
                            String httpRequestFirstLine = httpRequestSplit[0];
                            String httpRequestBody = "";
                            if (httpRequestSplit.length == 2) {
                                httpRequestBody = httpRequestSplit[1];
                            }

                            router.route(httpRequestFirstLine, httpRequestBody);

                        } else if (key.isWritable()) {
                            SocketChannel client = (SocketChannel) key.channel();
                            ByteBuffer outputBuffer = (ByteBuffer) key.attachment();


                            System.out.println("About to send the following to client: " + client.getRemoteAddress() + " from " + client.getLocalAddress());
                            String httpResponse = new String(outputBuffer.array()).trim();
                            System.out.println(httpResponse);
                            System.out.println();
                            outputBuffer.flip();

                            while (client.write(outputBuffer) > 0) ; //write to client


                            client.close();
                        }

                    } else if (key.channel() instanceof DatagramChannel) {
                        //Channel uses ByteBuffer, but we have to use DatagramPacket to keep destination information
                        if (key.isReadable()) {
                            datagramChannel = (DatagramChannel) key.channel();
                            DatagramPacket inputDatagramPacket = (DatagramPacket) key.attachment();


                            //If it is a request route the request
                            //If its a response give it to the model that is expecting the response

                            //Receive Datagram data and sender's address
                            ByteBuffer inputDatagramData = ByteBuffer.allocate(DATAGRAM_BYTE_SIZE);
                            InetSocketAddress senderAddress = (InetSocketAddress) datagramChannel.receive(inputDatagramData);
                            inputDatagramPacket.setSocketAddress(senderAddress); //Set destination address for reply
                            inputDatagramPacket.setData(inputDatagramData.array());  //You have to pass this to the controller, probably also the model

                            //Convert datagram data to string
                            String datagramString = new String(inputDatagramPacket.getData()).trim();
                            System.out.println("Datagram Packet received from: " + senderAddress + " sent to " + datagramChannel.getLocalAddress() + " contains: ");
                            System.out.println(datagramString);
                            System.out.println("\n");

                            //Get first line of datagram
                            String[] datagramSplit = datagramString.split("\n", 2);
                            String datagramHeader = datagramSplit[0];

                            String datagramBody = "";
                            if (datagramSplit.length == 2) {
                                datagramBody = datagramSplit[1].trim();
                            }

                            String[] headerWords = datagramHeader.split(" ");
                            String firstHeaderWord = headerWords[0];

                            int indexOfFirstLine = datagramBody.indexOf("\n") + 1;
                            datagramBody = datagramBody.substring(indexOfFirstLine);
                            String[] datagramValues = datagramBody.split("\n");

                            if (firstHeaderWord.equals("RESPONSE")) {
                                int packetNumber = Integer.parseInt(headerWords[4]);
                                StationController controller = controllerAwaitingResponse.get(packetNumber);
                                controller.receiveResponse(headerWords, datagramValues);
                                //Send to model based on packetNo.
                                //Let existing model/view/controller deal with it
                            } else {
                                //Create a new station/model/view

                                StationModel udpModel = new StationModel(datagramChannelKey, this, false, senderAddress, datagramValues);//key == datagramChannelKey
                                StationView udpView = new StationView();
                                StationController controller = new StationController(udpModel, udpView, datagramChannelKey, this, false, senderAddress, datagramValues); //View probs not necessary
                                //Route the datagram to correct response depending based on datagrams first line
                                Router router = new Router(controller);
                                router.route(datagramHeader, datagramBody);
//
                            }
                            //Find out what it is, feed info back to correct

                            ///NOT NECESSARILY!!!!!!!!!!!!!!!!

                        } else if (key.isWritable()) {
                            DatagramChannel datagramChan = (DatagramChannel) key.channel();
                            DatagramPacket outputDatagramPacket = (DatagramPacket) key.attachment();

                            //get address to send datagram response to
                            InetSocketAddress destinationAddress = (InetSocketAddress) outputDatagramPacket.getSocketAddress();
                            //get data of the datagram response you want to send
                            byte[] datagramOutputBytes = outputDatagramPacket.getData();
                            ByteBuffer datagramOutput = ByteBuffer.wrap(datagramOutputBytes);

                            //Need flip?
                            System.out.println("Sending datagram packet to: " + destinationAddress.getPort() + " from " + myUdpPort + " containing: ");
                            System.out.println(new String(datagramOutputBytes));
                            System.out.println("\n");

                            datagramChan.send(datagramOutput, destinationAddress);

                            //Do we now want to clear Channel?

                            key.interestOps(SelectionKey.OP_READ);

                        }
                    } else {
                        throw new Exception("Channel is not a SocketChannel or a DatagramChannel");
                    }

                }

            }

        } catch (Exception e) {
            System.out.println("Err: " + e);
            System.exit(1);
        }
    }

}