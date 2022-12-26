package searchengine.tools;

public class Tools {
    public static String getTime(long sec) {
        String str = "";
        if(sec >= 3_600){
            str += String.format("%02d", sec / 3_600) + ":";
            sec %= 3_600;
        }
        else {
            str += "00:";
        }
        if (sec >= 60){
            str += String.format("%02d", sec / 60) + ":";
            sec %= 60;
        }
        else {
            str += "00:";
        }
        return str + String.format("%02d", sec);
    }
}
