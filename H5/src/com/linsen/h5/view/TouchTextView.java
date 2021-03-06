package com.linsen.h5.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.Region;
import android.util.FloatMath;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.WindowManager;
import android.widget.TextView;

import com.linsen.h5.R;
import com.linsen.h5.utils.BitmapHelper;

public class TouchTextView extends TextView {

	float x_down = 0;
	float y_down = 0;
	// 起始点
	PointF startP = new PointF();
	PointF oMidP = new PointF();

	PointF mid = new PointF();// 中心点
	// 原始位置
	PointF tL = new PointF();// 左上点
	PointF tR = new PointF();// 右上点
	PointF bL = new PointF();// 左下点
	PointF bR = new PointF();// 右下点

	float scale = 1.0f;// 放大缩小
	float rotate = 0.0f;// 旋转角度
	// 移动距离
	float transX = 0.0f;
	float transY = 0.0f;

	float oldDist = 1f;
	float oldRotation = 0;
	Matrix matrix = new Matrix();
	Matrix matrix1 = new Matrix();
	Matrix savedMatrix = new Matrix();

	private static final int NONE = 0;
	private static final int DRAG = 1;
	private static final int ZOOM = 2;
	int mode = NONE;

	Paint textPaint;
	int textColor;
	float textSize;
	Paint mGuidelinePaint;

	boolean start = true;
	// 起始宽高
	float textWidth = 0f;
	float textHeight = 0f;
	Bitmap scaleBtn;
	Bitmap textBmp;
	// 当前宽度
	float cTextWidth = 0f;

	int screenWidth = 0;
	int screenHeight = 0;
	int scaleBtnWidth = 40;

	TextView textView;
	Context context;
	String text;

	Matrix scaleBtnMatrix;

	private CallBackInterface callBackInterface;

	public interface CallBackInterface {
		/** position 0表示点击文字框外 1表示点击文字框内 **/
		public void onClick(int position);
	}

	public TouchTextView(Context context, TextView textView) {
		super(context);
		this.textView = textView;
		this.context = context;
		init();
	}

	private void init() {
		text = textView.getText().toString();
		textColor = textView.getTextColors().getDefaultColor();
		textSize = textView.getTextSize();

		startP.x = textView.getX();
		startP.y = textView.getY();

		textPaint = new Paint();
		textPaint.setColor(textColor);
		textPaint.setAntiAlias(true);
		textPaint.setTextSize(textSize);
		textPaint.setTextAlign(Paint.Align.LEFT);
		textPaint.setTypeface(textView.getTypeface());

		mGuidelinePaint = new Paint();
		mGuidelinePaint.setColor(Color.RED);
		mGuidelinePaint.setStrokeWidth(2);
		mGuidelinePaint.setAntiAlias(true);

		matrix = new Matrix();
		matrix.postTranslate(textView.getX(), textView.getY());

		scaleBtnMatrix = new Matrix();

		scaleBtnWidth = BitmapHelper.dip2px(context, 30.0f);
		scaleBtn = BitmapFactory.decodeResource(getResources(),
				R.drawable.btn_scale);
		scaleBtn = Bitmap.createScaledBitmap(scaleBtn, scaleBtnWidth,
				scaleBtnWidth, true);

		WindowManager manager = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		Display display = manager.getDefaultDisplay();
		screenWidth = display.getWidth();
		screenHeight = display.getHeight();
	}

	protected void onDraw(Canvas canvas) {
		// 画布变幻
		if (start) {
			drawText(canvas);
			start = false;
		}
		canvas.save();
		canvas.drawBitmap(textBmp, matrix, null);
		canvas.drawLine(tL.x, tL.y, tR.x, tR.y, mGuidelinePaint);
		canvas.drawLine(bL.x, bL.y, bR.x, bR.y, mGuidelinePaint);
		canvas.drawLine(tL.x, tL.y, bL.x, bL.y, mGuidelinePaint);
		canvas.drawLine(bR.x, bR.y, tR.x, tR.y, mGuidelinePaint);

		scaleBtnMatrix.setTranslate(tR.x - scaleBtnWidth / 2, tR.y
				- scaleBtnWidth / 2);
		scaleBtnMatrix.postRotate(getRotate(), tR.x, tR.y);
		canvas.drawBitmap(scaleBtn, scaleBtnMatrix, null);
		// canvas.drawBitmap(scaleBtn, tR.x - scaleBtnWidth / 2, tR.y
		// - scaleBtnWidth / 2, null);
		canvas.restore();
	}

	public void drawText(Canvas canvas) {

		int lineCount = 0;
		if (text == null)
			return;
		char[] textCharArray = text.toCharArray();
		// 已绘的宽度
		float drawedWidth = 0;
		float charWidth;
		for (int i = 0; i < textCharArray.length; i++) {
			charWidth = textPaint.measureText(textCharArray, i, 1);
			if (textCharArray[i] == '\n') {
				lineCount++;
				if (drawedWidth > textWidth) {
					textWidth = drawedWidth;
				}
				drawedWidth = 0;
				continue;
			}
			drawedWidth += charWidth;
		}

		if (drawedWidth > textWidth) {
			textWidth = drawedWidth;
		}
		textHeight = (lineCount + 1) * textSize + 5;
		cTextWidth = textWidth;

		textBmp = Bitmap.createBitmap((int) textWidth, (int) textHeight,
				Bitmap.Config.ARGB_8888);
		Canvas canvasTemp = new Canvas(textBmp);
		canvasTemp.drawColor(Color.TRANSPARENT);

		lineCount = 0;
		drawedWidth = 0;
		for (int i = 0; i < textCharArray.length; i++) {
			charWidth = textPaint.measureText(textCharArray, i, 1);
			if (textCharArray[i] == '\n') {
				lineCount++;
				if (drawedWidth > textWidth) {
					textWidth = drawedWidth;
				}
				drawedWidth = 0;
				continue;
			}
			canvasTemp.drawText(textCharArray, i, 1, drawedWidth,
					(lineCount + 1) * textSize, textPaint);
			drawedWidth += charWidth;
		}

		tL.set(startP.x, startP.y);
		tR.set(textWidth + tL.x, tL.y);
		bL.set(tL.x, textHeight + tL.y);
		bR.set(textWidth + tL.x, textHeight + tL.y);

		float x = tL.x + bR.x;
		float y = tL.y + bR.y;
		oMidP.set(x / 2, y / 2);
		mid.set(x / 2, y / 2);

	}

	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction() & MotionEvent.ACTION_MASK) {
		case MotionEvent.ACTION_DOWN:
			x_down = event.getX();
			y_down = event.getY();
			savedMatrix.set(matrix);
			if (checkScaleBtnRegion(x_down, y_down)) {
				getParent().requestDisallowInterceptTouchEvent(true);
				mode = ZOOM;
				oldDist = spacing(event);
				oldRotation = rotation(event);
				savedMatrix.set(matrix);
				midPoint(mid);
			} else if (checkRectRegion(x_down, y_down)) {
				getParent().requestDisallowInterceptTouchEvent(true);
				mode = DRAG;
			} else {
				mode = NONE;
				getParent().requestDisallowInterceptTouchEvent(false);
			}
			break;
		case MotionEvent.ACTION_MOVE:
			if (mode == ZOOM) {
				matrix1.set(savedMatrix);
				float rotation = rotation(event) - oldRotation;
				float newDist = spacing(event);
				float scale = newDist / oldDist;
				matrix1.postScale(scale, scale, mid.x, mid.y);// 縮放
				matrix1.postRotate(rotation, mid.x, mid.y);// 旋轉
				updatePointPosition(matrix1);
				matrix.set(matrix1);
				invalidate();

			} else if (mode == DRAG) {
				matrix1.set(savedMatrix);
				matrix1.postTranslate(event.getX() - x_down, event.getY()
						- y_down);// 平移
				midPoint(mid);
				updatePointPosition(matrix1);
				matrix.set(matrix1);
				invalidate();
			}
			break;
		case MotionEvent.ACTION_UP:
			if (mode == NONE) {
				if (checkIsClick(x_down, y_down, event)) {
					if (callBackInterface != null) {
						callBackInterface.onClick(0);
					}
				}
			}
			if (mode == DRAG) {
				if (checkIsClick(x_down, y_down, event)) {
					if (callBackInterface != null) {
						callBackInterface.onClick(1);
					}
				}
			}
			Log.e("TAG_TRANS_ROTATE_SCALE", "TRANSX=" + getTransX()
					+ ",TRANSY=" + getTransY() + ",ROTATE=" + getRotate()
					+ ",SCALE=" + getScale());
			break;
		}
		return true;
	}

	private void updatePointPosition(Matrix matrix) {
		float[] f = new float[9];
		matrix.getValues(f);
		// 图片4个顶点的坐标
		tL.x = f[0] * 0 + f[1] * 0 + f[2];
		tL.y = f[3] * 0 + f[4] * 0 + f[5];
		tR.x = f[0] * textBmp.getWidth() + f[1] * 0 + f[2];
		tR.y = f[3] * textBmp.getWidth() + f[4] * 0 + f[5];
		bL.x = f[0] * 0 + f[1] * textBmp.getHeight() + f[2];
		bL.y = f[3] * 0 + f[4] * textBmp.getHeight() + f[5];
		bR.x = f[0] * textBmp.getWidth() + f[1] * textBmp.getHeight() + f[2];
		bR.y = f[3] * textBmp.getWidth() + f[4] * textBmp.getHeight() + f[5];
		// 图片现宽度
		cTextWidth = (float) Math.sqrt((tL.x - tR.x) * (tL.x - tR.x)
				+ (tL.y - tR.y) * (tL.y - tR.y));
	}

	private boolean checkScaleBtnRegion(float downX, float downY) {
		double distance = Math.sqrt((downX - tR.x) * (downX - tR.x)
				+ (downY - tR.y) * (downY - tR.y));
		if (distance > scaleBtnWidth / 2) {
			return false;
		} else {
			return true;
		}
	}

	// 判断点是否落在文本区域内
	private boolean checkRectRegion(float downX, float downY) {
		Path path = new Path();
		path.moveTo(tL.x, tL.y);
		path.lineTo(tR.x, tR.y);
		path.lineTo(bR.x, bR.y);
		path.lineTo(bL.x, bL.y);
		path.close();
		Region resultRegion = new Region();
		Region region = new Region(0, 0, screenWidth, screenHeight);
		resultRegion.setPath(path, region);// 与前面的path所构成的路径取交集，并将两交集设置为最终区域
		if (resultRegion.contains((int) downX, (int) downY)) {
			return true;
		} else {
			return false;
		}
	}

	private boolean checkIsClick(float downX, float downY, MotionEvent event) {
		double distance = Math.sqrt((downX - event.getX())
				* (downX - event.getX()) + (downY - event.getY())
				* (downY - event.getY()));
		if (distance < 5) {
			return true;
		} else {
			return false;
		}
	}

	// 触碰两点间距离
	private float spacing(MotionEvent event) {
		float x = event.getX() - mid.x;
		float y = event.getY() - mid.y;
		return (float) (2 * Math.sqrt(x * x + y * y));
	}

	// 取手势中心点
	private void midPoint(PointF point) {
		float x = tL.x + bR.x;
		float y = tL.y + bR.y;
		point.set(x / 2, y / 2);
	}

	// 取旋转角度
	private float rotation(MotionEvent event) {
		double delta_x = (mid.x - event.getX());
		double delta_y = (mid.y - event.getY());
		double radians = Math.atan2(delta_y, delta_x);
		return (float) Math.toDegrees(radians);
	}

	public float getScale() {
		return cTextWidth / textWidth;
	}

	public void setScale(float scale) {
		this.scale = scale;
	}

	public float getRotate() {
		double delta_x = (tR.x - tL.x);
		double delta_y = (tR.y - tL.y);
		double rotate = Math.atan2(delta_y, delta_x);
		return (float) Math.toDegrees(rotate);
	}

	public void setRotate(float rotate) {
		this.rotate = rotate;
	}

	public float getTransX() {
		return mid.x - oMidP.x;
	}

	public void setTransX(float transX) {
		this.transX = transX;
	}

	public float getTransY() {
		return mid.y - oMidP.y;
	}

	public void setTransY(float transY) {
		this.transY = transY;
	}

	public void reDrawText() {
		// 变换顺序不可改变
		matrix.postScale(scale, scale, oMidP.x, oMidP.y);
		matrix.postRotate(rotate, oMidP.x, oMidP.y);
		matrix.postTranslate(transX, transY);
		matrix1.set(matrix);
		updatePointPosition(matrix1);
		invalidate();
	}

	public int getTextColor() {
		return textColor;
	}

	public void setTextColor(int textColor) {
		this.textColor = textColor;
	}

	public float getTextSize() {
		return textSize;
	}

	public void setTextSize(float textSize) {
		this.textSize = textSize;
	}

	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// TODO Auto-generated method stub
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		setMeasuredDimension(measureHeight(widthMeasureSpec),
				measureHeight(heightMeasureSpec));
	}

	private int measureHeight(int mesureSize) {
		// TODO Auto-generated method stub
		int result = 0; // 结果
		int specMode = MeasureSpec.getMode(mesureSize);
		int specSize = MeasureSpec.getSize(mesureSize);
		switch (specMode) {
		case MeasureSpec.AT_MOST: // 子容器可以是声明大小内的任意大小
			result = specSize;
			break;
		case MeasureSpec.EXACTLY: // 父容器已经为子容器设置了尺寸,子容器应当服从这些边界,不论子容器想要多大的空间.
									// 比如EditTextView中的DrawLeft
			result = specSize;
			break;
		case MeasureSpec.UNSPECIFIED: // 父容器对于子容器没有任何限制,子容器想要多大就多大.
										// 所以完全取决于子view的大小
			result = specSize;
			break;
		default:
			break;
		}
		return result;
	}

	public PointF getStartP() {
		return startP;
	}

	public void setCallBackInterface(CallBackInterface callBackInterface) {
		this.callBackInterface = callBackInterface;
	}

	public void updateTextView(TextView textView) {
		this.textView = textView;
		text = textView.getText().toString();
		textColor = textView.getTextColors().getDefaultColor();
		textSize = textView.getTextSize();
		textPaint.setColor(textColor);
		textPaint.setTextSize(textSize);
		//float lineHeight = textView.getLineHeight();
		// Log.e("updateTextView", "lineSpacing = " + lineSpacing +
		// ",textSize = "+textSize);

		textWidth = 0;

		int lineCount = 0;
		if (text == null)
			return;
		char[] textCharArray = text.toCharArray();
		// 已绘的宽度
		float drawedWidth = 0;
		float charWidth;

		for (int i = 0; i < textCharArray.length; i++) {
			charWidth = textPaint.measureText(textCharArray, i, 1);
			if (textCharArray[i] == '\n') {
				lineCount++;
				if (drawedWidth > textWidth) {
					textWidth = drawedWidth;
				}
				drawedWidth = 0;
				continue;
			}
			drawedWidth += charWidth;
		}

		if (drawedWidth > textWidth) {
			textWidth = drawedWidth;
		}
		textHeight = (lineCount + 1) * textSize + 5;
		cTextWidth = textWidth;

//		textHeight = lineHeight * (lineCount + 1);
//		textWidth = textView.getWidth();
//		cTextWidth = textWidth;

		textBmp = Bitmap.createBitmap((int) textWidth, (int) textHeight,
				Bitmap.Config.ARGB_8888);
		Canvas canvasTemp = new Canvas(textBmp);
		canvasTemp.drawColor(Color.TRANSPARENT);

		lineCount = 0;
		drawedWidth = 0;
		for (int i = 0; i < textCharArray.length; i++) {
			charWidth = textPaint.measureText(textCharArray, i, 1);
			if (textCharArray[i] == '\n') {
				lineCount++;
				if (drawedWidth > textWidth) {
					textWidth = drawedWidth;
				}
				drawedWidth = 0;
				continue;
			}
			if (lineCount == 0) {
				canvasTemp.drawText(textCharArray, i, 1, drawedWidth,
						(lineCount + 1) * textSize, textPaint);
			}else{
				canvasTemp.drawText(textCharArray, i, 1, drawedWidth,
						(lineCount + 1) * textSize, textPaint);

			}
			drawedWidth += charWidth;
		}
		updatePointPosition(matrix);
		midPoint(mid);
		invalidate();
	}

}
