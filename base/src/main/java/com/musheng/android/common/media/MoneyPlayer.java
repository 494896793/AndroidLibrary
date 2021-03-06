package com.musheng.android.common.media;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.text.TextUtils;

import com.musheng.android.common.log.MSLog;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * Author      : MuSheng
 * CreateDate  : 2019/11/29 10:59
 * Description :
 */
public class MoneyPlayer {

    private static MoneyPlayer moneyPlayer = new MoneyPlayer();

    private MoneyPlayer() {
    }

    public static MoneyPlayer getInstance(){
        return moneyPlayer;
    }

    public void play(final Context context, final int money){
        new Thread(){
            @Override
            public void run() {
                super.run();
                List<String> strings = new ArrayList<>();
                strings.add("voice/money_lianhe.mp3");
                strings.addAll(readIntPart(String.valueOf(money)));
                strings.add("voice/money_cny.mp3");
                play(context, strings);
            }
        }.start();
    }

    public void play(final Context context, final String money){
        MSLog.d("prepare " + money);
        new Thread(){
            @Override
            public void run() {
                super.run();
                List<String> strings = new ArrayList<>();
                strings.add("voice/money_lianhe.mp3");
                String[] split = money.split("\\.");
                if(split.length > 0){
                    strings.addAll(readIntPart(split[0]));
                }
                if(split.length > 1){
                    strings.add("voice/money_pot.mp3");
                    strings.addAll(readStringPart(split[1]));
                }
                strings.add("voice/money_cny.mp3");

                MSLog.d("start " + money);
                play(context, strings);
                MSLog.d("stop " + money);
            }
        }.start();
    }

    public void play(final Context context, final List<String> voicePlay){
        synchronized (MoneyPlayer.this) {

            final MediaPlayer mMediaPlayer = new MediaPlayer();
            final CountDownLatch mCountDownLatch = new CountDownLatch(1);
            AssetFileDescriptor assetFileDescription = null;

            try {
                final int[] counter = {0};

                assetFileDescription = context.getAssets().openFd(voicePlay.get(counter[0]));

                mMediaPlayer.setDataSource(
                        assetFileDescription.getFileDescriptor(),
                        assetFileDescription.getStartOffset(),
                        assetFileDescription.getLength());

                mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        mMediaPlayer.start();
                    }
                });
                mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mediaPlayer) {
                        mediaPlayer.reset();
                        counter[0]++;

                        if (counter[0] < voicePlay.size()) {
                            try {
                                AssetFileDescriptor fileDescription2 = context.getAssets().openFd(voicePlay.get(counter[0]));
                                mediaPlayer.setDataSource(
                                        fileDescription2.getFileDescriptor(),
                                        fileDescription2.getStartOffset(),
                                        fileDescription2.getLength());
                                mediaPlayer.prepare();
                            } catch (IOException e) {
                                e.printStackTrace();
                                mCountDownLatch.countDown();
                            }
                        } else {
                            mediaPlayer.release();
                            mCountDownLatch.countDown();
                        }
                    }
                });

                mMediaPlayer.prepareAsync();

            } catch (Exception e) {
                e.printStackTrace();
                mCountDownLatch.countDown();
            } finally {
                if (assetFileDescription != null) {
                    try {
                        assetFileDescription.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            try {
                mCountDownLatch.await();
                notifyAll();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


    private static final char[] NUM = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};
    private static final char[] CHINESE_UNIT = {'???', '???', '???', '???', '???', '???', '???', '???', '???', '???', '???', '???'};

    /**
     * ???????????????????????????????????????,???????????????
     */
    private String readInt(int moneyNum) {
        String res = "";
        int i = 0;
        if (moneyNum == 0) {
            return "0";
        }

        if (moneyNum == 10) {
            return "???";
        }

        if (moneyNum > 10 && moneyNum < 20) {
            return "???" + moneyNum % 10;
        }

        while (moneyNum > 0) {
            res = CHINESE_UNIT[i++] + res;
            res = NUM[moneyNum % 10] + res;
            moneyNum /= 10;
        }

        return res.replaceAll("0[?????????]", "0")
                .replaceAll("0+???", "???")
                .replaceAll("0+???", "???")
                .replaceAll("0+???", "???")
                .replaceAll("0+", "0")
                .replace("???", "");
    }


    /**
     * ???????????????????????????
     *
     * @param integerPart
     * @return
     */
    private List<String> readIntPart(String integerPart) {
        List<String> result = new ArrayList<>();
        String intString = readInt(Integer.parseInt(integerPart));
        int len = intString.length();
        for (int i = 0; i < len; i++) {
            char current = intString.charAt(i);
            if (current == '???') {
                result.add("voice/money_10.mp3");

            } else if (current == '???') {
                result.add("voice/money_100.mp3");

            } else if (current == '???') {
                result.add("voice/money_1000.mp3");

            } else if (current == '???') {
                result.add("voice/money_10000.mp3");

            } else if (current == '???') {
                result.add("voice/money_100000000.mp3");

            } else {
                result.add("voice/money_" + current + ".mp3");
            }
        }
        return result;
    }

    private List<String> readStringPart(String stringPart){
        List<String> result = new ArrayList<>();
        if(!TextUtils.isEmpty(stringPart)){
            for(int i = 0; i < stringPart.length(); i++){
                int charIndex = stringPart.charAt(i);
                if(charIndex >= 48 && charIndex <= 57){
                    result.add("voice/money_" + (charIndex - 48) + ".mp3");
                }
            }
        }
        return result;
    }

}

