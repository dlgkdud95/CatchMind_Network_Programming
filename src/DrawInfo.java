import java.io.Serializable;

public class DrawInfo implements Serializable { //Serializeable 인터페이스를 추가 -> 클래스의 인스턴스를 직렬화 할 수 있어서 오류가 발생하지 않고 객체를 전송할 수 있음
    private int startX, startY;
    private int endX, endY;
    private int x, y;

    public void setStartX(int startX) {this.startX = startX;}

    public void setStartY(int startY) {this.startY = startY;}

    public void setEndX(int endX) {this.endX = endX;}

    public void setEndY(int endY) {this.endY = endY;}

    public int getStartX() {return startX;}

    public int getStartY() {return startY;}

    public int getEndX() {return endX;}

    public int getEndY() {return endY;}

    public void setY(int y) {
        this.y = y;
    }

    public int getY() {
        return y;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getX() {
        return x;
    }
}
