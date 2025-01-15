package com.github.barteksc.pdfviewer.scroll;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.R;
import com.github.barteksc.pdfviewer.util.Util;

public class DefaultScrollHandle extends RelativeLayout implements ScrollHandle {
    private static final int DEFAULT_TEXT_SIZE = 16;
    private static final int HANDLE_LONG = 65;
    private static final int HANDLE_SHORT = 40;
    protected Context context;
    private float currentPos;
    private Handler handler;
    private Runnable hidePageScrollerRunnable;
    private boolean inverted;
    private PDFView pdfView;
    private float relativeHandlerMiddle;
    protected TextView textView;

    public DefaultScrollHandle(Context context2) {
        this(context2, false);
    }

    public DefaultScrollHandle(Context context2, boolean inverted2) {
        super(context2);
        this.relativeHandlerMiddle = 0.0f;
        this.handler = new Handler();
        this.hidePageScrollerRunnable = new Runnable() {
            public void run() {
                DefaultScrollHandle.this.hide();
            }
        };
        this.context = context2;
        this.inverted = inverted2;
        this.textView = new TextView(context2);
        setVisibility(4);
        setTextColor(ViewCompat.MEASURED_STATE_MASK);
        setTextSize(16);
    }

    public void setupLayout(PDFView pdfView2) {
        Drawable background;
        int align;
        int height;
        int width;
        if (pdfView2.isSwipeVertical()) {
            width = 65;
            height = 40;
            if (this.inverted) {
                align = 9;
                background = ContextCompat.getDrawable(this.context, R.drawable.default_scroll_handle_left);
            } else {
                align = 11;
                background = ContextCompat.getDrawable(this.context, R.drawable.default_scroll_handle_right);
            }
        } else {
            width = 40;
            height = 65;
            if (this.inverted) {
                align = 10;
                background = ContextCompat.getDrawable(this.context, R.drawable.default_scroll_handle_top);
            } else {
                align = 12;
                background = ContextCompat.getDrawable(this.context, R.drawable.default_scroll_handle_bottom);
            }
        }
        if (Build.VERSION.SDK_INT < 16) {
            setBackgroundDrawable(background);
        } else {
            setBackground(background);
        }
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(Util.getDP(this.context, width), Util.getDP(this.context, height));
        lp.setMargins(0, 0, 0, 0);
        RelativeLayout.LayoutParams tvlp = new RelativeLayout.LayoutParams(-2, -2);
        tvlp.addRule(13, -1);
        addView(this.textView, tvlp);
        lp.addRule(align);
        pdfView2.addView(this, lp);
        this.pdfView = pdfView2;
    }

    public void destroyLayout() {
        this.pdfView.removeView(this);
    }

    public void setScroll(float position) {
        if (!shown()) {
            show();
        } else {
            this.handler.removeCallbacks(this.hidePageScrollerRunnable);
        }
        setPosition(((float) (this.pdfView.isSwipeVertical() ? this.pdfView.getHeight() : this.pdfView.getWidth())) * position);
    }

    private void setPosition(float pos) {
        float pdfViewSize;
        if (!Float.isInfinite(pos) && !Float.isNaN(pos)) {
            if (this.pdfView.isSwipeVertical()) {
                pdfViewSize = (float) this.pdfView.getHeight();
            } else {
                pdfViewSize = (float) this.pdfView.getWidth();
            }
            float pos2 = pos - this.relativeHandlerMiddle;
            if (pos2 < 0.0f) {
                pos2 = 0.0f;
            } else if (pos2 > pdfViewSize - ((float) Util.getDP(this.context, 40))) {
                pos2 = pdfViewSize - ((float) Util.getDP(this.context, 40));
            }
            if (this.pdfView.isSwipeVertical()) {
                setY(pos2);
            } else {
                setX(pos2);
            }
            calculateMiddle();
            invalidate();
        }
    }

    private void calculateMiddle() {
        float pdfViewSize;
        float viewSize;
        float pos;
        if (this.pdfView.isSwipeVertical()) {
            pos = getY();
            viewSize = (float) getHeight();
            pdfViewSize = (float) this.pdfView.getHeight();
        } else {
            pos = getX();
            viewSize = (float) getWidth();
            pdfViewSize = (float) this.pdfView.getWidth();
        }
        this.relativeHandlerMiddle = ((this.relativeHandlerMiddle + pos) / pdfViewSize) * viewSize;
    }

    public void hideDelayed() {
        this.handler.postDelayed(this.hidePageScrollerRunnable, 1000);
    }

    public void setPageNum(int pageNum) {
        String text = String.valueOf(pageNum);
        if (!this.textView.getText().equals(text)) {
            this.textView.setText(text);
        }
    }

    public boolean shown() {
        return getVisibility() == 0;
    }

    public void show() {
        setVisibility(0);
    }

    public void hide() {
        setVisibility(4);
    }

    public void setTextColor(int color) {
        this.textView.setTextColor(color);
    }

    public void setTextSize(int size) {
        this.textView.setTextSize(1, (float) size);
    }

    private boolean isPDFViewReady() {
        PDFView pDFView = this.pdfView;
        return pDFView != null && pDFView.getPageCount() > 0 && !this.pdfView.documentFitsView();
    }

    /* JADX WARNING: Removed duplicated region for block: B:25:0x005d  */
    /* JADX WARNING: Removed duplicated region for block: B:26:0x0078  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean onTouchEvent(android.view.MotionEvent r6) {
        /*
            r5 = this;
            boolean r0 = r5.isPDFViewReady()
            if (r0 != 0) goto L_0x000b
            boolean r0 = super.onTouchEvent(r6)
            return r0
        L_0x000b:
            int r0 = r6.getAction()
            r1 = 1
            if (r0 == 0) goto L_0x0029
            if (r0 == r1) goto L_0x0025
            r2 = 2
            if (r0 == r2) goto L_0x0054
            r2 = 3
            if (r0 == r2) goto L_0x0025
            r2 = 5
            if (r0 == r2) goto L_0x0029
            r2 = 6
            if (r0 == r2) goto L_0x0025
            boolean r0 = super.onTouchEvent(r6)
            return r0
        L_0x0025:
            r5.hideDelayed()
            return r1
        L_0x0029:
            com.github.barteksc.pdfviewer.PDFView r0 = r5.pdfView
            r0.stopFling()
            android.os.Handler r0 = r5.handler
            java.lang.Runnable r2 = r5.hidePageScrollerRunnable
            r0.removeCallbacks(r2)
            com.github.barteksc.pdfviewer.PDFView r0 = r5.pdfView
            boolean r0 = r0.isSwipeVertical()
            if (r0 == 0) goto L_0x0049
            float r0 = r6.getRawY()
            float r2 = r5.getY()
            float r0 = r0 - r2
            r5.currentPos = r0
            goto L_0x0054
        L_0x0049:
            float r0 = r6.getRawX()
            float r2 = r5.getX()
            float r0 = r0 - r2
            r5.currentPos = r0
        L_0x0054:
            com.github.barteksc.pdfviewer.PDFView r0 = r5.pdfView
            boolean r0 = r0.isSwipeVertical()
            r2 = 0
            if (r0 == 0) goto L_0x0078
            float r0 = r6.getRawY()
            float r3 = r5.currentPos
            float r0 = r0 - r3
            float r3 = r5.relativeHandlerMiddle
            float r0 = r0 + r3
            r5.setPosition(r0)
            com.github.barteksc.pdfviewer.PDFView r0 = r5.pdfView
            float r3 = r5.relativeHandlerMiddle
            int r4 = r5.getHeight()
            float r4 = (float) r4
            float r3 = r3 / r4
            r0.setPositionOffset(r3, r2)
            goto L_0x0092
        L_0x0078:
            float r0 = r6.getRawX()
            float r3 = r5.currentPos
            float r0 = r0 - r3
            float r3 = r5.relativeHandlerMiddle
            float r0 = r0 + r3
            r5.setPosition(r0)
            com.github.barteksc.pdfviewer.PDFView r0 = r5.pdfView
            float r3 = r5.relativeHandlerMiddle
            int r4 = r5.getWidth()
            float r4 = (float) r4
            float r3 = r3 / r4
            r0.setPositionOffset(r3, r2)
        L_0x0092:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle.onTouchEvent(android.view.MotionEvent):boolean");
    }
}
