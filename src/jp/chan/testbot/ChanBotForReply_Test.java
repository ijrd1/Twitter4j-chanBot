package jp.chan.testbot;

import jp.chan.MessageFilter;
import jp.keys.Keys;
import twitter4j.*;
import twitter4j.auth.AccessToken;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

/**
 * テスト用
 */
public class ChanBotForReply_Test {

    private static final String FROMBOT = " --- ちゃんbotより";

    public static void main(String[] args) {

        Twitter twitter = TwitterFactory.getSingleton();
        twitter.setOAuthConsumer(Keys.CONSUMERKEY.toString(), Keys.CONSUMERSECRET.toString());
        twitter.setOAuthAccessToken(new AccessToken(Keys.ACCESSTOKEN.toString(), Keys.ACCESSTOKENSECRET.toString()));

        try {
            /**
             * 自分とのやりとり
             */
            ResponseList<Status> statuses = twitter.getUserTimeline(); // 全タイムライン読み込み
            List<String> idList = new ArrayList<>(); // ターゲットさんのつぶやきID
            List<String> messList = new ArrayList<>(); // ターゲットさんがつぶやいた内容

            StatusFilter(statuses, idList, messList);

            BigInteger target = new BigInteger(idList.get(0)); // 一番最新のid取得
            System.out.println(target + " :: " + messList.get(0));

            /**
             * Who's the target, ME or not.
             */
            String annoTarget =
                    messList.get(0).substring(1,2).equals("@")
                            ? messList.get(0).split(" ")[0].substring(2): Keys.ME.toString();

            // TODO メンション -> 自つぶやき -> bot で返信する時、annoTarget 再設定問題 (行ナンバーずらし)

            /**
             * target is not ME
             */
            if (!annoTarget.equals(Keys.ME.toString())) {
                // 変数入れ替え
                statuses = twitter.getMentionsTimeline(); // メンションのみ読み込み
                idList = new ArrayList<>();
                messList = new ArrayList<>();

                StatusFilter(statuses, idList, messList);

                target = new BigInteger(idList.get(0));
                System.out.println(target + " :: " + messList.get(0));
            }

            int idx = 0;
            String targetMess = messList.get(idx);
            MessageFilter messageFilter = new MessageFilter();

            // Test 臨時書き換え
            targetMess = "明日の天気\n" +
                    "ソート[32 3 2 -1]\n" +
                    "いなごんもください"; // TODO これに対応できるように MessageFilter += 連結工夫

            String botReply = messageFilter.makeReply(targetMess);

            // 更新される内容をセット
            System.out.println("予想されるメッセージ " + "@" + annoTarget + " " + botReply + FROMBOT);
            // 画像がある場合
            if (messageFilter.getPicFlag()) System.out.println("画像 Flag ON");
            // 更新
            System.out.println("ターゲット ID " + target.longValue());


            System.out.println("yes ok !");

        } catch (TwitterException e) {
            System.out.println("no !" + e.getMessage());
        }
    }

    /**
     * 様々なステータスに対応する凡用フィルター
     *
     * @param statuses Twitter Statuses のうちどれか
     * @param idList  長い桁の ID リスト
     * @param messList ID ごとのメッセージリスト
     */
    private static void StatusFilter(ResponseList<Status> statuses, List<String> idList, List<String> messList)
    {
        for (Status s : statuses) {
            String[] statusArray = s.toString().split(",");
            idList.add(statusArray[1].trim().substring(3));
            messList.add(statusArray[2].trim().substring(5));
        }
    }

}
