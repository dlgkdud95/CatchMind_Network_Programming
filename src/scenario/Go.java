package scenario;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Go {
    public static void main(String[] args) throws IOException  {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        int score = 0;

        while (true){
            String str = reader.readLine();
            Game game = new Game(str, score);
            game.Question();
            score = game.score;

            if(str.equals("end")) break;
        }
    }
}
