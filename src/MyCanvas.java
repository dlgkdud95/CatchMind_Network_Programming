import java.awt.*;
import java.awt.image.BufferedImage;

public class MyCanvas extends Canvas {
    int startX;
    int startY;
    int endX;
    int endY;
    Color cr = Color.black;
    private Graphics2D graphics2D; //그림을 그리는 펜 역할
    private BufferedImage paintImage;


    public MyCanvas() {
        setPreferredSize(new Dimension(600,585));
        paintImage = new BufferedImage(getPreferredSize().width, getPreferredSize().height, BufferedImage.TYPE_INT_ARGB);
        graphics2D = paintImage.createGraphics();
    }
    public void paint (Graphics g){ //그림이 그려질 때마다 BufferedImage에 업데이트 해줌
        g.drawImage(paintImage, 0,0, null); //지금까지 그린 걸 paintImage에 저장
    }
    public void update (Graphics g){
        paint(g);
    }
    public void DrawLine ( int startX, int startY, int endX, int endY){
        graphics2D.setColor(cr);
        graphics2D.setStroke(new BasicStroke(5, BasicStroke.CAP_ROUND, 0));
        graphics2D.drawLine(startX, startY, endX, endY); //선을 그림.

        repaint(); //다시 그리기 위해 호출
    }
}

/*
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class MyCanvas extends Canvas {

    public MyCanvas(){

    }
    int x = -50;
    int y = -50;

    public int w = 7, h = 7;
    Color cr = Color.black;

    public void paint(Graphics g){
        g.setColor(cr);
        g.fillOval(x,y,w,h);
    }

    public void update(Graphics g){
        paint(g);
    }

}
*/

//class MyMouseListener extends MouseAdapter{
//    public MyCanvas canvas = new MyCanvas();
//
////    public void mousePressed(MouseEvent e){
////        int x = e.getX();
////        int y = e.getY();
////
////    }
//
//    public void mouseDragged(MouseEvent e){
//
//        int xx = e.getX();
//        int yy = e.getY();
//
//        canvas.x = xx;
//        canvas.y = yy;
//
//        canvas.repaint();
////        canvas.update(canvas.getGraphics());
//
//    }
//
//    public void mouseReleased(MouseEvent e){
//
//    }
//}
