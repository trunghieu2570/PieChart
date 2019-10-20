package kdh.demo.piechart;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;
import java.util.Random;

public class PieChart extends View {

    public enum AnimType {NO_ANIMATION, TOGETHER, SEQUENTIALLY}

    public class Item {

        private float startAngle;
        private float sweepAngle = 0;
        private int color;
        private float percent;

        Item(float startAngle, int color) {
            this.startAngle = startAngle;
            this.color = color;
        }

    }

    private Paint piePaint = null;
    private Paint textPaint = null;
    private ArrayList<Item> items = null;
    ArrayList<Animator> animators;
    AnimatorSet animSet;
    private RectF bound;
    private float cx;
    private float cy;
    private int radius;

    public PieChart(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        items = new ArrayList<Item>();
        initPaint();
        initAnimator();
        int[] ints = {1};
        updateAngle(ints, AnimType.NO_ANIMATION);
    }

    private void initPaint() {
        //piePaint
        piePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        piePaint.setColor(Color.RED);
        piePaint.setStyle(Paint.Style.FILL);
        piePaint.setStrokeWidth(5);
        //textPaint
        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(50);
        Typeface f = Typeface.create("", Typeface.BOLD);
        textPaint.setTypeface(f);
        textPaint.setTextAlign(Paint.Align.CENTER);

    }

    private void initAnimator() {
        animators = new ArrayList<Animator>();
        animSet = new AnimatorSet();

    }

    private int getRandomColor() {
        Random rnd = new Random();
        int color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
        return color;
    }


    public void updateAngle(int[] values, AnimType animType) {
        switch (animType) {
            case TOGETHER:
                updateAngleTogether(values);
                break;
            case SEQUENTIALLY:
                updateAngleSequentially(values);
                break;
            default:
                updateAngleNoAnimation(values);
                break;
        }
    }

    public void updateAngleNoAnimation(int[] values) {
        animSet.cancel();
        items.clear();
        int sum = 0;
        float currentAngle = 0;
        int color = 0;
        for (int value : values) {
            sum += value;
        }
        float u = 360f / sum;
        float p = 100f / sum;
        Random rnd = new Random();
        for (int value : values) {
            color = getRandomColor();
            final Item item = new Item(currentAngle, color);
            currentAngle += value * u;
            item.sweepAngle = currentAngle;
            item.percent = value * p;
            items.add(item);

        }
        Collections.reverse(items);
        invalidate();
    }

    public void updateAngleTogether(int[] values) {
        animSet.cancel();
        items.clear();
        animators.clear();
        int sum = 0;
        float currentAngle = 0;
        int color = 0;
        for (int value : values) {
            sum += value;
        }
        float u = 360f / sum;
        float p = 100f / sum;
        Random rnd = new Random();
        for (int value : values) {
            color = getRandomColor();

            final Item item = new Item(currentAngle, color);
            item.percent = value * p;
            currentAngle += value * u;
            //add animator
            ValueAnimator anim = ValueAnimator.ofFloat(0, currentAngle);
            anim.setDuration(1000);
            anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    item.sweepAngle = (float) animation.getAnimatedValue();
                    invalidate();
                }
            });
            items.add(item);
            animators.add(anim);

        }

        Collections.reverse(items);
        Collections.reverse(animators);

        animSet.playTogether(animators);
        animSet.start();
    }

    public void updateAngleSequentially(int[] values) {
        animSet.cancel();
        items.clear();
        animators.clear();
        int sum = 0;
        float currentAngle = 0;
        int color = 0;
        for (int value : values) {
            sum += value;
        }
        float u = 360f / sum;
        float p = 100f / sum;
        Random rnd = new Random();
        for (int value : values) {
            color = getRandomColor();

            final Item item = new Item(currentAngle, color);
            currentAngle += value * u;
            item.percent = value * p;
            //add animator
            ValueAnimator anim = ValueAnimator.ofFloat(0, currentAngle);
            anim.setDuration(3000 / values.length);
            anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    item.sweepAngle = (float) animation.getAnimatedValue();
                    invalidate();
                }
            });
            items.add(item);
            animators.add(anim);
        }

        Collections.reverse(items);
        Collections.reverse(animators);

        animSet.playSequentially(animators);
        animSet.start();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        cx = (getX() + w) / 2;
        cy = (getY() + h) / 2;
        radius = w < h ? w / 2 : h / 2;
        bound = new RectF(cx - radius, cy - radius, cx + radius, cy + radius);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for (Item item : items) {
            piePaint.setColor(item.color);
            piePaint.setStyle(Paint.Style.FILL);
            canvas.drawArc(bound, -90, item.sweepAngle, true, piePaint);
            piePaint.setColor(Color.WHITE);
            piePaint.setStyle(Paint.Style.STROKE);
            canvas.drawArc(bound, -90, item.sweepAngle, true, piePaint);
            float medianAngle = (item.startAngle - 90 + ((item.sweepAngle - item.startAngle) / 2f)) * (float) Math.PI / 180f;
            canvas.drawText(String.format(Locale.ENGLISH, "%.1f", item.percent) + "%", (float) (cx + (radius / 1.5f * Math.cos(medianAngle))), (float) (cy + (radius / 1.5f * Math.sin(medianAngle))), textPaint);

        }
    }
}
