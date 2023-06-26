import scenario.Quiz;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Vector;

public class ChatServer {
    private Socket socket;
    private static MyCanvas canvas;

    //클라이언트의 정보를 담는 리스트
    public static ArrayList<ClientThread> clients = new ArrayList<>();  //클라이언트 리스트

    public static String Question(){
        Quiz quiz = new Quiz();
        String answer = quiz.setQuiz();

        return answer;
    }

    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket(8000); //8000번 포트로 서버 소켓 새엇ㅇ
            System.out.println("서버가 시작되었습니다.");

            Question();

            while (true) {
                Socket clientSocket = serverSocket.accept(); //클라이언트 연결 대기
                System.out.println("클라이언트가 접속하였습니다.");

                ClientThread clientThread = new ClientThread(clientSocket); //클라이언트 스레드 생성
                clients.add(clientThread); // 클라이언트를 리스트에 추가
                clientThread.start(); //클라이언트 스레드 실행
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

//클라이언트
class ClientThread extends Thread {
    public static ArrayList<String> qList = new ArrayList<String>(); // 문제 랜덤 선택
    static String answer = ChatServer.Question(); // 서버의 Question() 메서드를 호출하여 문제를 선택
    private Socket clientSocket; // 클라이언트 소켓
    private BufferedReader in;
    private BufferedWriter out;
    private ObjectInputStream ois; // 객체를 읽어오기 위한 ObjectInputStream
    private ObjectOutputStream oos; // 객체를 전송하기 위한 ObjectOutputStream

    int score = 0; // 점수
    int ready; // 게임 준비 여부
    int turn = 0; // 게임 턴
    String myTurn = ""; // 출제자의 이름
    public Vector userName = new Vector(); // 사용자 이름을 저장하는 Vector

    // 생성자
    public ClientThread(Socket clientSocket) throws IOException {
        this.clientSocket = clientSocket;
        try {
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
            /*oos = new ObjectOutputStream(clientSocket.getOutputStream());
            ois = new ObjectInputStream(clientSocket.getInputStream());*/
            this.setDaemon(true); // 데몬 스레드로 설정
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 게임 시작
    public void gameStart() {
        int count = ChatServer.clients.size();
        if ((count % 3 == 0) && (count != 0)) { //클라이언트가 3의 배수일 떼 게임 실행
            try {
                for (int i = 3; i > 0; i--) {
                    sendMessageToAllClients("[" + i + "초 후 게임을 시작합니다 . . . ]");
                    Thread.sleep(1000);
                }
                sendMessageToAllClients("출제자는 " + myTurn + "입니다.");
                ChatServer.clients.get(turn).sendMessage("제시어는 [ " + answer + " ]입니다. 자유롭게 그림판에 표현해보세요~");
                sendMessageToAllClients("문제를 맞춰보십시오.");
            } catch (InterruptedException | IOException ie) {
            }
        }
    }

    // 턴 설정
    String setTurn() {
        int count = ChatServer.clients.size();
        turn = (int) (Math.random() * count); //랜덤으로 턴 결정
        myTurn = qList.get(turn);
        return myTurn;
    }

    // 스레드 실행
    public void run() {
        try {
            while (true) {
                /*ois = new ObjectInputStream(clientSocket.getInputStream());

                DrawInfo drawInfo = (DrawInfo) ois.readObject();
                sendMousePointToAllClients(drawInfo);*/

                String name = in.readLine(); // 클라이언트로부터 이름을 읽어옴
                System.out.println("[" + name + "] 님이 입장하였습니다.");
                sendMessageToAllClients("[" + name + "] 님이 입장하였습니다.");
                userName.add(name); // 사용자 이름을 Vector에 추가

                Iterator<String> it = userName.iterator(); //Vector에 있는 사용자 이름 qList에 추가
                while (it.hasNext()) {
                    qList.add(it.next()); // Vector에 있는 사용자 이름을 qList에 추가
                }

                if (ChatServer.clients.size() == 3) {
                    setTurn(); // 턴 설정
                }

                gameStart(); // 게임 시작

                String inputMsg;
                while ((inputMsg = in.readLine()) != null) {
                    System.out.println("[" + name + "] " + inputMsg);

                    sendMessageToAllClients(name + ": " + inputMsg);

                    if (answer.equals(inputMsg)) {
                        sendMessageToAllClients("정답! [ " + answer + " ]");
                        score++;
                        sendMessageToAllClients(name + "의 현재 점수는 " + score);
                        if (score == 3) {
                            sendMessageToAllClients(name + " win !!! 먼저 3점을 획득하여 이겼습니다.");
                            break;
                        }
                        sendMessageToAllClients("계속 하시겠습니까 ? (y/n)");
                        inputMsg = in.readLine();
                        String yn = "y";
                        if (yn.equals(inputMsg)) {
                            this.answer = ChatServer.Question();
                            setTurn();
                            gameStart();
                        } else
                            break;
                    } else if (inputMsg.equals("end")) {
                        sendMessageToAllClients("끝냅니다.");
                        break;
                    } else {
                        sendMessageToAllClients("땡! 다시 생각해보세요 ~");
                    }
                }

                System.out.println("[" + name + "] 클라이언트가 퇴장하였습니다.");
                sendMessageToAllClients("[" + name + "] 클라이언트가 퇴장하였습니다.");

                // 스트림과 소켓 닫기
                in.close();
                ois.close();
                out.close();
                oos.close();
                clientSocket.close();

                // 클라이언트가 퇴장한 경우 클라이언트를 리스트에서 제거
                removeClient(this);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 모든 클라이언트에게 메시지를 전송하는 메서드
    public static synchronized void sendMessageToAllClients(String message) throws IOException {
        for (ClientThread client : ChatServer.clients) {
            client.sendMessage(message);
        }
    }

    // 모든 클라이언트에게 이미지를 전송하는 메서드
    public synchronized void sendMousePointToAllClients(DrawInfo drawInfo) throws IOException {
        for (ClientThread client : ChatServer.clients) {
            if (drawInfo != null) {
                try {
                    //MyObjectOutputStream oos = new MyObjectOutputStream(client.clientSocket.getOutputStream());
                    ObjectOutputStream oos = new ObjectOutputStream(client.clientSocket.getOutputStream());
                    client.oos.writeObject(drawInfo);
                    client.oos.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // 클라이언트에게 메시지를 전송하는 메서드
    public void sendMessage(String message) throws IOException {
        out.write(message + "\n");
        out.flush();
    }

    // 클라이언트가 퇴장한 경우 클라이언트를 리스트에서 제거
    public static synchronized void removeClient(ClientThread client) {

        ChatServer.clients.remove(client);
    }


    /*// 객체 전송 시 StreamHeader를 쓰지 않도록 오버라이딩한 ObjectOutputStream 클래스
    class MyObjectOutputStream extends ObjectOutputStream {
        MyObjectOutputStream() throws IOException {
            super();
        }

        MyObjectOutputStream(OutputStream o) throws IOException {
            super(o);
        }

        public void writeStreamHeader() throws IOException {
            return;
        }
    }*/
}