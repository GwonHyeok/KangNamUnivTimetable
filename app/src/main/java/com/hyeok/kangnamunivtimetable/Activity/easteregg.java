package com.hyeok.kangnamunivtimetable.Activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

import com.hyeok.kangnamunivtimetable.R;
import com.romainpiel.shimmer.Shimmer;
import com.romainpiel.shimmer.ShimmerTextView;

public class easteregg extends Activity implements View.OnLongClickListener {
    private boolean ch_1 = true, ch_2 = true, ch_3 = true, ch_4 = true;
    LinearLayout mainView;
    private ShimmerTextView myShimmerTextView, myShimmerTextView_01, myShimmerTextView_02, myShimmerTextView_03, wereshimmer;
    private HorizontalScrollView easteregg_scrollview, easteregg_scrollview_01, easteregg_scrollview_02, easteregg_scrollview_03;
    private String name[] = {"강성묵", "권순기", "권재환", "권혁", "김남현", "김다슬", "김도연", "김명호", "김상욱", "김선길",
            "김선화", "김성민", "김성현", "김승민", "김영준", "김영탁", "김용범", "김우종", "김유진", "김은지",
            "김은지", "김인태", "김종현", "김주원", "김지수", "김지환", "김태호", "김형문", "목동윤", "박민영",
            "박소정", "박수진", "박은비", "박준범", "박현아", "배명환", "배상윤", "변지홍", "서홍일", "손세인",
            "안지훈", "안필환", "양석원", "오영진", "오택관", "유창무", "유희수", "윤경서", "이경훈", "이동훈",
            "이승원", "이연우", "이우란", "이은선", "이주연", "이진영", "이주연", "이현섭", "이형우", "임소정",
            "정승수", "조안나", "조은서", "조현준", "조휘원", "차동인", "차현빈", "채지은", "최현우", "최혜선",
            "최훈", "한창현", "홍지영", "홍지은", "황태영", "권용록", "함영빈", "양효영", "이재석", "장재훈",
            "김호", "임소담이", "박지희", "임명준", "최연우"};

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.easteregg);
        //noinspection ConstantConditions
        getActionBar().hide();
        viewInit();
        shimmerviewScroll();
        shimmerWork();

    }

    @Override
    public boolean onLongClick(View view) {
        /**
         * LongClickListener
         * Custom New Easter Egg
         */
        return false;
    }

    private void shimmerWork() {
        Shimmer shimmer = new Shimmer();
        shimmer.setDuration(1000)
                .setStartDelay(200)
                .setDirection(Shimmer.ANIMATION_DIRECTION_RTL);

        Shimmer shimmer1 = new Shimmer();
        shimmer1.setDuration(1000)
                .setStartDelay(300)
                .setDirection(Shimmer.ANIMATION_DIRECTION_RTL);

        Shimmer shimmer2 = new Shimmer();
        shimmer2.setDuration(1000)
                .setStartDelay(400)
                .setDirection(Shimmer.ANIMATION_DIRECTION_RTL);

        Shimmer shimmer3 = new Shimmer();
        shimmer3.setDuration(1000)
                .setStartDelay(500)
                .setDirection(Shimmer.ANIMATION_DIRECTION_RTL);


        shimmer.start(myShimmerTextView);
        shimmer1.start(myShimmerTextView_01);
        shimmer2.start(myShimmerTextView_02);
        shimmer3.start(myShimmerTextView_03);
        shimmer.start(wereshimmer);
    }

    private void shimmerviewScroll() {
        CountDownTimer ct = new CountDownTimer(4800, 20) {
            @Override
            public void onTick(long mills) {
                if (ch_1)
                    easteregg_scrollview.scrollTo((int) (5000 - mills), 0);
                else
                    easteregg_scrollview.scrollTo((int) mills, 0);
            }

            @Override
            public void onFinish() {
                ch_1 = !ch_1;
                start();

            }
        };

        CountDownTimer ct1 = new CountDownTimer(5000, 20) {
            @Override
            public void onTick(long mills) {
                if (ch_2)
                    easteregg_scrollview_01.scrollTo((int) (5000 - mills), 0);
                else
                    easteregg_scrollview_01.scrollTo((int) mills, 0);
            }

            @Override
            public void onFinish() {
                start();
                ch_2 = !ch_2;
            }
        };

        CountDownTimer ct2 = new CountDownTimer(5200, 20) {
            @Override
            public void onTick(long mills) {
                if (ch_3)
                    easteregg_scrollview_02.scrollTo((int) (5000 - mills), 0);
                else
                    easteregg_scrollview_02.scrollTo((int) mills, 0);
            }

            @Override
            public void onFinish() {
                start();
                ch_3 = !ch_3;
            }
        };

        final CountDownTimer ct3 = new CountDownTimer(5800, 20) {
            @Override
            public void onTick(long mills) {
                if (ch_4)
                    easteregg_scrollview_03.scrollTo((int) (6000 - mills), 0);
                else
                    easteregg_scrollview_03.scrollTo((int) mills, 0);
            }

            @Override
            public void onFinish() {
                start();
                ch_4 = !ch_4;
            }
        };
        ct.start();
        ct1.start();
        ct2.start();
        ct3.start();
    }

    private void viewInit() {
        myShimmerTextView = (ShimmerTextView) findViewById(R.id.shimmer_tv);
        myShimmerTextView_01 = (ShimmerTextView) findViewById(R.id.ShimmerTextView01);
        myShimmerTextView_02 = (ShimmerTextView) findViewById(R.id.ShimmerTextView02);
        myShimmerTextView_03 = (ShimmerTextView) findViewById(R.id.shimmer_tv03);
        wereshimmer = (ShimmerTextView) findViewById(R.id.shimmer_were_tv);
        easteregg_scrollview = (HorizontalScrollView) findViewById(R.id.eastereggScrollView);
        easteregg_scrollview_01 = (HorizontalScrollView) findViewById(R.id.HorizontalScrollView01);
        easteregg_scrollview_02 = (HorizontalScrollView) findViewById(R.id.HorizontalScrollView02);
        easteregg_scrollview_03 = (HorizontalScrollView) findViewById(R.id.eastereggScrollView03);
        mainView = (LinearLayout) findViewById(R.id.eg_linearlayout);
        mainView.setOnLongClickListener(this);
        String tmp = "", tmp1 = "", tmp2 = "", tmp3 = "";
        for (int i = 0; i != 21; i++) {
            tmp += name[i] + " ";
        }
        for (int i = 21; i != 42; i++) {
            tmp1 += name[i] + " ";
        }
        for (int i = 42; i != 63; i++) {
            tmp2 += name[i] + " ";
        }
        for (int i = 60; i != name.length; i++) {
            tmp3 += name[i] + " ";
        }
        myShimmerTextView.setText(tmp);
        myShimmerTextView_01.setText(tmp1);
        myShimmerTextView_02.setText(tmp2);
        myShimmerTextView_03.setText(tmp3);
    }
}
