package com.cntt.freemusicdownloadnow.ui.customview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.widget.ImageView;
import mp3onlinepro.trungpt.com.mp3onlinepro.R;

/**
 * Created by trung on 04/29/2016.
 */
public class ArcImageView extends ImageView
{
    private Paint paint;
    int strokedWidth = 0;
    private float sweepAngle = 0.0F;

    public ArcImageView(Context paramContext)
    {
        super(paramContext);
        init(paramContext);
    }

    public ArcImageView(Context paramContext, AttributeSet paramAttributeSet)
    {
        super(paramContext, paramAttributeSet);
        init(paramContext);
    }

    public ArcImageView(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
    {
        super(paramContext, paramAttributeSet, paramInt);
        init(paramContext);
    }

    private void init(Context paramContext)
    {
        this.strokedWidth = ((int)(paramContext.getResources().getDisplayMetrics().density * 1.5F));
        this.paint = new Paint();
        this.paint.setAntiAlias(true);
        this.paint.setStyle(Paint.Style.STROKE);
        this.paint.setColor(getResources().getColor(R.color.green_light));
        this.paint.setStrokeWidth(this.strokedWidth);
    }

    protected void onDraw(Canvas paramCanvas)
    {
        super.onDraw(paramCanvas);
        RectF localRectF = new RectF();
        localRectF.left = this.strokedWidth;
        localRectF.top = this.strokedWidth;
        localRectF.right = (getWidth() - this.strokedWidth);
        localRectF.bottom = (getHeight() - this.strokedWidth);
        paramCanvas.drawArc(localRectF, 270.0F, this.sweepAngle, false, this.paint);
    }

    public void setProgress(float paramFloat)
    {
        float f = paramFloat;
        if (paramFloat < 0.0F) {
            f = 0.0F;
        }
        if (paramFloat > 1.0F) {
            f = 1.0F;
        }
        paramFloat = 360.0F * f;
        if (this.sweepAngle != paramFloat)
        {
            this.sweepAngle = paramFloat;
            postInvalidate();
        }
    }
}
