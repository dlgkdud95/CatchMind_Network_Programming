package scenario;

public class Game {
    public int score = 0;
    Quiz quiz;
    String answer = null;
    String str = null;

    public Game(String str, int score) {
        this.str = str;
        this.score = score;
    }


    // 게임 전개
    public void Question(){

        quiz = new Quiz();
        answer = quiz.setQuiz();

        // while (str != "null"){
        if(answer.equals(str)) {
            System.out.println("정답! [ " + answer + " ]");
            score++;
        }
        else if(str.equals("end")) {
            System.out.println("끝냅니다.");
        }
        else if(score > 5){
            System.out.println("you win !!!");
        }
        else{
            System.out.println("땡! 정답은 " + answer );
        }
        System.out.println("현재 점수는 " + score);
    }
}