package scenario;

public class Quiz {
    String[] answer = new String[20];

    public Quiz(){
        answer[1] = "책";
        answer[2] = "학생";
        answer[3] = "감";
        answer[4] = "집";
        answer[5] = "학교";
        answer[6] = "옷장";
        answer[7] = "바나나";
        answer[8] = "딸기";
        answer[9] = "냉장고";
        answer[10] = "고슴도치";
        answer[11] = "노트북";
        answer[12] = "마우스";
        answer[13] = "바지";
        answer[14] = "밤";
        answer[15] = "햄버거";

    }

    //문제 랜덤 생성
    public String setQuiz(){
        int num = (int)(Math.random() * 15) + 1;

        return answer[num];
    }
}