import scenario.Quiz;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.Socket;

public class ClientView extends JFrame implements ActionListener{


    private ObjectOutputStream oos;
    private ObjectInputStream ois;
    private DrawInfo drawInfo;
    private int startX, endX;
    private int startY, endY;
    private Graphics graphics;
    private Graphics2D graphics2D = null;
    private JPanel contentPane;
    private JScrollPane scrollPane; //스크롤창
    private JTextPane chatView; //채팅 뷰 (전송 받은 메시지 출력)
    private JTextField messageInput; //(메시지 입력 칸)
    private JButton btnSend;
    private BufferedReader in = null;
    private BufferedWriter out = null;
    private Socket socket = null;
    private SendThread send = null;
    private String name;
    Quiz quiz;

    public Button btn = new Button();
    public static MyCanvas can;
    static Color color = Color.BLACK;

    public ClientView() {
        setTitle("캐치 마인드");
        setResizable(false); //창 사이즈 조절 불가로 설정
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //x클릭하면 종료
        setSize(1290,710);

        contentPane = new JPanel();
        contentPane.setBackground(new Color(0,204,204));
        setContentPane(contentPane);
        contentPane.setLayout(null);

        scrollPane = new JScrollPane();
        scrollPane.setBounds(936,71,330,537);
        contentPane.add(scrollPane);

        chatView = new JTextPane();
        chatView.setEditable(false);
        scrollPane.setViewportView(chatView); //JScrollPanel 전용 add

        messageInput = new JTextField();
        messageInput.setBounds(936,616,267,40);
        contentPane.add(messageInput);
        messageInput.setColumns(10);
        messageInput.addActionListener(this);

        btnSend = new JButton("SEND");
        btnSend.setBackground(Color.PINK);
        btnSend.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
        btnSend.setBounds(1211, 616, 55, 40);
        contentPane.add(btnSend);
        btnSend.addActionListener(this);

        can = new MyCanvas();
        can.setBounds(324,71,600,585);
        can.setBackground(Color.white);

        paintOnCanvas paint = new paintOnCanvas();
        can.addMouseMotionListener(paint);
        can.addMouseListener(paint);
        contentPane.add(can);



        btn.btnClear.setBounds(518,23,100,40);
        btn.btnClear.setBackground(Color.PINK);
        contentPane.add(btn.btnClear);

        btn.btnColor.setBounds(630,23,100,40);
        btn.btnColor.setBackground(Color.PINK);
        contentPane.add(btn.btnColor);

        setVisible(true);

        try {
            socket = new Socket("localhost", 8000); //서버에 연결
            out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())); //서버와 스트림 연결
            in = new BufferedReader(new InputStreamReader(socket.getInputStream())); //서버와 스트림 연결

            connectToServer(socket);
            //can.addMouseMotionListener(new SendThread(socket,userName));
            /*oos = new ObjectOutputStream(socket.getOutputStream());
            ois = new ObjectInputStream(socket.getInputStream());*/
        } catch (IOException e) {
            e.printStackTrace();
        }

        quiz = new Quiz();
    }

    static class MenuActionListener implements ActionListener {
        public void actionPerformed(ActionEvent e){
            JButton b = (JButton)e.getSource();
            if(b.getText().equals("색상")){
                Color selectedColor = JColorChooser.showDialog(null,"Color",Color.YELLOW);
                can.cr = selectedColor;
                color = selectedColor;
            }

            else if(b.getText().equals("지우기")){
                can.cr = Color.white;
                color = Color.white;
            }
        }
    }

    //그림판에 그림을 그리기 위한 이벤트 리스너
    class paintOnCanvas implements MouseMotionListener, MouseListener {
        public void mouseDragged(MouseEvent e){ //마우스 드래그 되는 동안의 좌표 넘겨줌
            endX = e.getX();
            endY = e.getY();

            can.DrawLine(startX, startY, endX, endY);

            startX = e.getX();
            startY = e.getY();

//            sendDrawInfo(startX, startY, endX, endY);
        }

        @Override
        public void mouseMoved(MouseEvent e) {

        }

        public void mouseClicked(MouseEvent e) {  //마우스가 눌러진 위치에서 그대로 뗴어질 때 호출
        }
        @Override
        public void mousePressed(MouseEvent e) { //마우스 눌렸을 때 작동
            startX = e.getX(); //마우스 눌렸을 때 위치의 x좌표값 얻고 초기화
            startY = e.getY(); //마우스 눌렸을 때 위치의 y좌표값 얻고 초기화
        }

        @Override
        public void mouseReleased(MouseEvent e) { //마우스 클릭이 끝났을 때 작동
        }

        @Override
        public void mouseEntered(MouseEvent e) { //그림판 내에 진입 시 이벤트 처리

        }

        @Override
        public void mouseExited(MouseEvent e) {

        }
    }

    //마우스 좌표 정보 전송
//    private void sendDrawInfo(int startX, int startY, int endX, int endY) {
//        try {
//            ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
//            objectOutputStream.writeObject(new DrawInfo(startX, startY, endX, endY));
//            objectOutputStream.flush();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
    public static void main(String[] args) {
        ClientView chatView = new ClientView();
    }
    //서버 연결 메소드
    private void connectToServer(Socket socket) throws IOException  {
        try {
            System.out.println("[서버와 연결되었습니다.]");

            name = "user" + (int)(Math.random()*10);
            Thread sendThread = new SendThread(socket, name);
            //Thread receiveThread = new ReceiveThread(socket,userName);

            sendThread.setDaemon(true);
            sendThread.start();
            /*receiveThread.setDaemon(true);
            receiveThread.start();*/

            while (true) {
                String inputMsg = in.readLine();
                if (inputMsg == null) {
                    break;
                }
                System.out.println("From: " + inputMsg);
                AppendText(inputMsg);
            }
        } catch (IOException e) {
            System.out.println("[서버와 접속 끊김]");
            e.printStackTrace();
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        System.out.println("[서버와 연결종료]");
    }

    @Override //전송 버튼을 누르거나 엔터 키를 누르면 작동
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == btnSend || e.getSource() == messageInput) {
            String msg = messageInput.getText(); //입력된 메시지 가져오기

            try {
                out.write(msg + "\n"); //서버로 메시지 전송
                out.flush();
                AppendText("\n 나: "+ msg); //채팅 뷰에 메시지 띄우기
                messageInput.setText(""); //메시지 입력 창 초기화
                messageInput.requestFocus(); //메시지 입력 커서 초기화
                if("quit".equals(msg)) {
                    /*out.close();
                    in.close();
                    socket.close();*/
                    System.exit(0); //클라이언트 종료
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    //메시지창 왼쪽 정렬 해주는 메소드
    public synchronized void AppendText(String msg) {
        msg = msg.trim();  //양옆 메시지 공백 제거

        StyledDocument doc = chatView.getStyledDocument();
        SimpleAttributeSet left = new SimpleAttributeSet();
        StyleConstants.setAlignment(left, StyleConstants.ALIGN_LEFT); //채팅 왼쪽 정렬
        StyleConstants.setForeground(left, Color.BLACK); //폰트 색 지정
        doc.setParagraphAttributes(doc.getLength(), 1, left, false);
        try {
            doc.insertString(doc.getLength(), msg + "\n", left);
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }

    //SendThread 생성, 서버로부터 메시지 받아옴
    class SendThread extends Thread implements MouseMotionListener{
        private Socket socket = null;
        private String name;
        public SendThread(Socket socket, String name) throws IOException { //생성자
            this.socket = socket;
            this.name = name;
            /*oos = new ObjectOutputStream(socket.getOutputStream());
            ois = new ObjectInputStream(socket.getInputStream());*/
        }

        public void run() { //start() 메소드 사용 시 실행됨
            try {
                String msg;
                while ((msg = in.readLine()) != null) {
                    AppendText(msg);
                    int pos = messageInput.getText().length();
                    messageInput.setCaretPosition(pos);
                }
            } catch (IOException e) {
                System.out.println("[서버와 통신 중 오류 발생]");
                e.printStackTrace();
            }
        }
        //마우스 좌표 보냄
        @Override
       public void mouseDragged(MouseEvent e) {
            /*int x = e.getX();  //마우스가 드래그 되는 동안의 좌표
            int y = e.getY();
            drawInfo = new DrawInfo();
            drawInfo.setY(y);
            drawInfo.setX(x);
            try {
                oos.writeObject(drawInfo);
                oos.flush();
            } catch (IOException ex) {
                ex.printStackTrace();
            }*/
        }

        @Override
        public void mouseMoved(MouseEvent e) {

        }
    }

    //서버에서 클라이언트로
    /*class ReceiveThread extends Thread {
        Socket receiveSocket;
        String userName;

        ReceiveThread(Socket receiveSocket, String userName) throws IOException {
            this.receiveSocket = receiveSocket;
            oos = new ObjectOutputStream(this.receiveSocket.getOutputStream());
            ois = new ObjectInputStream(this.receiveSocket.getInputStream());
        }
        @Override
        public void run() {
            ChatInfo chatReceiveInfo = null;
            DrawInfo drawReceiveInfo;
            try {
                while (true) {
                    //String mousePoint = dis.readUTF();
                    chatReceiveInfo = (ChatInfo) ois.readObject();
                    Object mouseObj = ois.readObject();
                    drawReceiveInfo = (DrawInfo) mouseObj;

                    if(drawReceiveInfo.getSignal() == 2){
                        can.repaint();
                        list.add(drawReceiveInfo);
                    }

                    if (drawReceiveInfo.getSignal() == 3) {
                        oos.close();
                        ois.close();
                        socket.close();
                    }

                    //int tempX = Integer.parseInt(mousePoint.substring(mousePoint.indexOf(",") -1 , mousePoint.indexOf(",")));
                    //int tempY = Integer.parseInt(mousePoint.substring(mousePoint.indexOf(",")+1));

                    int tempX = drawReceiveInfo.getX();
                    int tempY = drawReceiveInfo.getY();
                    System.out.println(tempX + ", " + tempY);

                    graphics = can.getGraphics();
                    graphics2D = (Graphics2D) graphics;
                    graphics2D.setColor(color);
                    graphics.drawLine(tempX, tempY, tempX, tempY);
                }
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }*/
}