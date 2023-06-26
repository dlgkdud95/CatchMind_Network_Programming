import javax.swing.*;
import java.awt.*;

public class Button {
    static Color color = Color.BLACK;
    public MyCanvas can = new MyCanvas();
    public ClientView.MenuActionListener mal = new ClientView.MenuActionListener();
    public JButton btnColor = new JButton("색상"); //색상 버튼 생성

    public JButton btnClear = new JButton("지우기"); //지우기 버튼 생성

    public Button(){

        btnColor.addActionListener(mal);
        btnClear.addActionListener(mal);
    }


}
