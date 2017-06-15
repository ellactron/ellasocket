package rici.roplug.open.view;

import java.util.ArrayList;
import java.util.HashMap;

import com.rici.wifi.util.L;

import rici.roplug.open.R;
import rici.roplug.open.bll.BLLUtil;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.NinePatch;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class HistoryElectricityView extends View {
	public final static int VIEW_STYLE_SRCOLLLINE = 0;
	public final static int VIEW_STYLE_LINE = 1;
	public final static int VIEW_STYLE_BAR = 2;

	private int viewStyle = VIEW_STYLE_SRCOLLLINE;
	private int dayLineElectricity = 7;
	private int styleXCount = 12;
	private int barStyleXCount = 6;

	private boolean isShowY = true;
	private int xScale, xCount, yScale, yCount;
	private int left, right, top, bottom;
	private int displayWidth, displayHeight, viewWidth;
	private int labelTextSize = 34, showTextSize = 45, popTextSize = 40;
	private int labelTextColor = 0xffffffff, showTextColor = 0xfffacd89,
			popTextColor = 0xffffffff;
	private String unitString = null;

	private ArrayList<String> xArrayList = null;
	private ArrayList<String> yArrayList = null;
	private HashMap<String, Float> viewShowData = new HashMap<String, Float>();
	private boolean isInteger = false;
	private float maxData = 0f, unitY = 0.5f;
	private float drawLineSize = 6f;
	private int startDownX = 0;
	private int startDownY = 0;
	private String showItemValue;
	private int showItem = -1;
	private long lastTime = 0L;
	private boolean isLongClickMove = false;
	private boolean isFirstDraw = false;
	private ArrayList<Point> pointArrayList = new ArrayList<Point>();
	private Rect rect = new Rect(0, 0, 0, 0);
	private Resources res = getResources();
	private int yLabelOffset = 0;
	private float maxElectricity = 3.5f;
	private float uintElectricity = 0.5f;
	private Paint textPaint = null;
	private Paint linePaint = null;

	private int numberWidth = 0;
	private int numberHeight = 0;

	public HistoryElectricityView(Context context) {
		super(context);
		textPaint = new Paint();
		textPaint.setAntiAlias(true);
		textPaint.setStyle(Paint.Style.STROKE);

		linePaint = new Paint();
		linePaint.setAntiAlias(true);
		linePaint.setStyle(Paint.Style.STROKE);
		isFirstDraw = true;
	}

	public HistoryElectricityView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public void initDrawLocation(int leftX, int rightX, int topY, int bottomY) {
		this.left = leftX;
		this.right = rightX;
		this.top = topY;
		this.bottom = bottomY;
	}

	public void setShowStyle(int textSize, int textColor) {
		this.showTextSize = textSize;
		this.showTextColor = textColor;
	}

	public void setPopStyle(int textSize, int textColor) {
		this.popTextSize = textSize;
		this.popTextColor = textColor;
	}

	public void setLabelStyle(int textSize, int textColor) {
		this.labelTextSize = textSize + 2;
		this.labelTextColor = textColor;

		textPaint.setTextSize(labelTextSize);
		textPaint.setColor(labelTextColor);
	}

	public String getUnitString() {
		return unitString;
	}

	public void setUnitString(String unitString) {
		this.unitString = unitString;
	}

	public int getViewStyle() {
		return viewStyle;
	}

	public void setViewStyle(int viewStyle) {
		this.viewStyle = viewStyle;
	}

	public boolean isShowY() {
		return isShowY;
	}

	public void setShowY(boolean isShowY) {
		this.isShowY = isShowY;
	}

	public int getxCount() {
		return xCount;
	}

	public void setxCount(int xCount) {
		this.xCount = xCount;
	}

	public int getyCount() {
		return yCount;
	}

	public void setyCount(int yCount) {
		this.yCount = yCount;
	}

	public int getShowItem() {
		return showItem;
	}

	public void setShowItem(int showItem) {
		this.showItem = showItem;
	}

	public String getShowItemValue() {
		return showItemValue;
	}

	public void setShowItemValue(String showItemValue) {
		this.showItemValue = showItemValue;
	}

	public ArrayList<String> getxArrayList() {
		return xArrayList;
	}

	public void setxArrayList(ArrayList<String> xArrayList) {
		this.xArrayList = xArrayList;
	}

	public ArrayList<String> getyArrayList() {
		return yArrayList;
	}

	public void setyArrayList(ArrayList<String> yArrayList) {
		this.yArrayList = yArrayList;
	}

	private boolean isLongPressed(float lastX, float lastY, float curX,
			float curY, long lastDownTime, long curEventTime, long longPressTime) {
		float offsetX = Math.abs(curX - lastX);
		float offsetY = Math.abs(curY - lastY);
		long intervaltime = curEventTime - lastDownTime;
		if (offsetX <= 30 && intervaltime >= longPressTime) {
			return true;
		}
		return false;
	}

	private String getLongClickItem(float clickX, float clickY) {
		String clickString = null;
		int c = (int) (clickX - rect.left)
				/ R.integer.socket_electric_day_line_margin_h;
		clickString = String.valueOf(c);
		return clickString;
	}

	/**
	 * 画曲线
	 * 
	 * @param points
	 *            开始点和结束点
	 * @param canvas
	 *            画布
	 * @param paint
	 *            画笔
	 */
	private void drawscrollline(ArrayList<Point> points, Canvas canvas,
			Paint paint) {
		Point startp = new Point();
		Point endp = new Point();
		for (int i = 0; i < points.size() - 1; i++) {
			startp = points.get(i);
			endp = points.get(i + 1);
			int wt = (startp.x + endp.x) / 2;
			Point p3 = new Point();
			Point p4 = new Point();
			p3.y = startp.y;
			p3.x = wt;
			p4.y = endp.y;
			p4.x = wt;
			// 确定曲线的路径
			Path path = new Path();
			path.moveTo(startp.x, startp.y);
			path.cubicTo(p3.x, p3.y, p4.x, p4.y, endp.x, endp.y);
			// 绘制
			canvas.drawPath(path, paint);
		}
	}

	private void drawline(ArrayList<Point> points, Canvas canvas, Paint paint) {
		Point startp = new Point();
		Point endp = new Point();
		for (int i = 0; i < points.size() - 1; i++) {
			startp = points.get(i);
			endp = points.get(i + 1);
			canvas.drawLine(startp.x, startp.y, endp.x, endp.y, paint);
		}
	}

	public void drawNinepath(Canvas c, int id, Rect r1) {
		Bitmap bmp = BitmapFactory.decodeResource(getResources(), id);

		NinePatch patch = new NinePatch(bmp, bmp.getNinePatchChunk(), null);
		patch.draw(c, r1);
	}

	private void drawUnitString(Canvas canvas) {
		if (unitString != null) {
			int startDrawX = left + getScrollX();
			textPaint.setStrokeWidth(1f);
			textPaint.setStyle(Paint.Style.FILL);
			textPaint.setColor(labelTextColor);
			textPaint.setTextSize(labelTextSize);
			canvas.save();
			canvas.clipRect(getScrollX(), getScrollY(), startDrawX
					+ displayWidth, displayHeight - bottom + numberHeight / 2
					+ getScrollY());
			canvas.drawText(unitString, left + getScrollX(), top, textPaint);
			canvas.restore();
		}
	}

	private void drawYLabel(Canvas canvas) {
		int startDrawX = left + getScrollX();
		int startDrawY = displayHeight - bottom;
		String yLabelString = null;
		textPaint.setStrokeWidth(1f);
		textPaint.setStyle(Paint.Style.FILL);
		textPaint.setColor(labelTextColor);
		textPaint.setTextSize(labelTextSize);
		linePaint.setStrokeWidth(1f);
		linePaint.setColor(labelTextColor);

		canvas.save();
		canvas.clipRect(getScrollX(), getScrollY(), startDrawX + xCount
				* xScale + 1, displayHeight - bottom + numberHeight / 2
				+ getScrollY());
		for (int i = 0; i <= yCount; i++) {
			if (isInteger) {
				yLabelString = String.valueOf((int) unitY * i);
			} else {
				yLabelString = String.valueOf(unitY * i);
			}

			canvas.drawText(yLabelString, startDrawX + numberWidth
					- yLabelOffset - (int) textPaint.measureText(yLabelString),
					startDrawY + numberHeight / 2, textPaint);
			canvas.drawLine(startDrawX + numberWidth, startDrawY, startDrawX
					+ displayWidth, startDrawY, linePaint);
			startDrawY -= yScale;
		}
		canvas.restore();
	}

	private void drawXLabel(Canvas canvas) {
		int startDrawX = left + numberWidth;
		int startDrawY = getScrollY() + displayHeight - bottom;
		int drawHourY = startDrawY + numberHeight / 2
				+ (int) (getScrollY() + displayHeight - startDrawY) / 2;
		int hourStrLen = 0;
		Bitmap curBitmap = BitmapFactory.decodeResource(res,
				R.drawable.y335_bg_history_electricity_high);
		int cBitmapW = curBitmap.getWidth();
		int cBitmapH = curBitmap.getHeight();
		textPaint.setStrokeWidth(1f);
		textPaint.setTextSize(labelTextSize);
		canvas.save();
		canvas.clipRect(startDrawX + getScrollX() - cBitmapW / 2, getScrollY()
				+ top, left + displayWidth + numberWidth / 2 + getScrollX(),
				displayHeight + getScrollY());
		for (int i = 0; i < xArrayList.size(); i++) {
			// 画小时数
			hourStrLen = (int) textPaint.measureText(xArrayList.get(i));
			if (showItem >= 0 && showItem == i) {
				canvas.drawBitmap(curBitmap, startDrawX + i * xScale - cBitmapW
						/ 2, drawHourY - numberHeight / 2 - cBitmapH / 2, null);
				textPaint
						.setColor(res
								.getColor(R.color.history_electricity_item_select_color));
				canvas.drawText(xArrayList.get(i), startDrawX + i * xScale
						- hourStrLen / 2, drawHourY, textPaint);
			} else {
				textPaint.setColor(res.getColor(R.color.system_color_white));
				canvas.drawText(xArrayList.get(i), startDrawX + i * xScale
						- hourStrLen / 2, drawHourY, textPaint);
			}
		}
		canvas.restore();
		if (isShowY) {
			canvas.save();
			canvas.clipRect(startDrawX + getScrollX(), getScrollY() + top
					- numberHeight / 2, left + getScrollX() + displayWidth,
					displayHeight - bottom + numberHeight / 2 + getScrollY());

			// 画刻度线
			linePaint.setStrokeWidth(1f);
			linePaint.setColor(res.getColor(R.color.system_color_white));
			int degreeHeight = numberHeight / 3;
			for (int i = 0; i < xArrayList.size(); i++) {
				canvas.drawLine(left + numberWidth + i * xScale, displayHeight
						- bottom - degreeHeight, left + numberWidth + i
						* xScale, displayHeight - bottom, linePaint);
			}
			canvas.restore();
		}
	}

	private void drawBarXLabel(Canvas canvas) {
		Bitmap curBitmap = BitmapFactory.decodeResource(res,
				R.drawable.y335_history_electricity_year_normal);
		int cBitmapW = curBitmap.getWidth();

		if (xCount == 0 || xCount > barStyleXCount) {
			xCount = barStyleXCount;
		}
		int defaultMax = 100;
		defaultMax = Math.max(defaultMax, (int)this.maxData);
		defaultMax = (int)(defaultMax * 1.2f);
		xScale = (displayWidth - barStyleXCount * cBitmapW)
				/ (barStyleXCount - 1);
		xScale += cBitmapW;
		int startDrawX = left;
		int startDrawY = getScrollY() + displayHeight - bottom;
		int drawHourY = startDrawY + numberHeight / 2
				+ (int) (getScrollY() + displayHeight - startDrawY) / 2;

		int yearStrLen = 0;
		textPaint.setStrokeWidth(1f);
		textPaint.setTextSize(labelTextSize);
		canvas.save();
		canvas.clipRect(0 + getScrollX(),
				getScrollY() + top, startDrawX + getScrollX() + displayWidth
						+ numberWidth / 4, displayHeight + getScrollY());

		for (int i = 0; i < viewShowData.size(); i++) {
			yearStrLen = (int) textPaint.measureText(xArrayList.get(i));
			textPaint.setColor(res.getColor(R.color.system_color_white));
			canvas.drawText(
					String.valueOf(xArrayList.get(i)),
					Math.max(startDrawX + i * xScale + (cBitmapW - yearStrLen)
							/ 2, 0), drawHourY, textPaint);
			// 画条形框
			Rect yearRect = new Rect();
			yearRect.left = startDrawX + i * xScale
					+ Math.max(0, (yearStrLen - cBitmapW) / 2);
			yearRect.right = yearRect.left + cBitmapW;
			yearRect.bottom = displayHeight - bottom;
			yearRect.top = yearRect.bottom
					- Math.max((int) ((yearRect.bottom - top * 2) * (Float
							.valueOf(viewShowData.get(xArrayList.get(i))) / defaultMax)),1);
			
			if (i == showItem) {
				drawNinepath(canvas,
						R.drawable.y335_history_electricity_year_line_over,
						yearRect);
				// 画年用电量

				String showElectricityString = BLLUtil.DECIMAL_FORMAT.format(viewShowData
						.get(xArrayList.get(showItem)));

				Rect displayRestRect = new Rect(0, 0, 0, 0);
				Paint electricityPaint = new Paint();
				electricityPaint.setAntiAlias(true);
				electricityPaint.setStyle(Paint.Style.FILL);
				electricityPaint.setTypeface(Typeface.DEFAULT_BOLD);
				electricityPaint.setTextSize(showTextSize);
				electricityPaint.setColor(showTextColor);
				electricityPaint.getTextBounds(showElectricityString, 0,
						showElectricityString.length(), displayRestRect);
				int offsetX = i * xScale + (cBitmapW - displayRestRect.width())
						/ 2;
				canvas.drawText(showElectricityString, Math.max(0,startDrawX + offsetX),
						yearRect.top - displayRestRect.height(),
						electricityPaint);
			} else {
				drawNinepath(canvas,
						R.drawable.y335_history_electricity_year_line, yearRect);
			}
		}

		canvas.restore();
	}

	private void drawData(Canvas canvas) {
		int startDrawX = left + numberWidth;
		int startDrawY = displayHeight - bottom;

		pointArrayList.clear();
		for (int i = 0; i < viewShowData.size(); i++) {
			int stopX = startDrawX + i * xScale;
			int stopY = 0;
			if (viewShowData.containsKey(xArrayList.get(i))) {
				stopY = (int) (startDrawY - (yScale
						* Float.valueOf(viewShowData.get(xArrayList.get(i))) / unitY));
			}
			pointArrayList.add(new Point(stopX, stopY));
		}
		textPaint.setStrokeWidth(drawLineSize);
		textPaint.setStyle(Paint.Style.STROKE);
		textPaint.setColor(res.getColor(R.color.history_electric_draw_color));

		canvas.save();
		canvas.clipRect(startDrawX + getScrollX(), getScrollY() + top, left
				+ getScrollX() + displayWidth, displayHeight - bottom
				+ numberHeight / 2 + getScrollY());
		if (getViewStyle() == VIEW_STYLE_SRCOLLLINE) {
			drawscrollline(pointArrayList, canvas, textPaint);
		} else if (getViewStyle() == VIEW_STYLE_LINE) {
			drawline(pointArrayList, canvas, textPaint);
		}

		canvas.restore();
		// 显示当前用电量
		Bitmap bitmap = BitmapFactory.decodeResource(res,
				R.drawable.y335_bg_history_electricity_show_left);
		int bitmapW = bitmap.getWidth();
		int bitmapH = bitmap.getHeight();
		int showX = startDrawX + showItem * xScale;
		int showY = startDrawY;
		if (viewShowData.containsKey(xArrayList.get(showItem))) {
			showY = (int) (startDrawY - (yScale
					* Float.valueOf(viewShowData.get(xArrayList.get(showItem))) / unitY));
		}

		canvas.save();
		canvas.clipRect(Math.min(left, showX - bitmapW / 2)
				+ getScrollX(), getScrollY(), left + getScrollX()
				+ displayWidth+right, displayHeight
				+ getScrollY());
		bitmap = BitmapFactory.decodeResource(res,
				R.drawable.y335_history_electricity_current_circle);
		bitmapW = bitmap.getWidth();
		bitmapH = bitmap.getHeight();
		showX = (startDrawX + showItem * xScale) - bitmapW / 2;

		canvas.drawBitmap(bitmap, showX, (int) (showY - bitmapH / 3), null);

		bitmap = BitmapFactory.decodeResource(res,
				R.drawable.y335_bg_history_electricity_show_left);
		bitmapW = bitmap.getWidth() * 109/136;
		bitmapH = bitmap.getHeight();
		showX = (startDrawX + showItem * xScale)+ numberWidth/3;
		String showElectricityString = "";
		showElectricityString = BLLUtil.DECIMAL_FORMAT.format(viewShowData.get(xArrayList
				.get(showItem)));
		Rect displayRestRect = new Rect(0, 0, 0, 0);
		Paint electricityPaint = new Paint();
		electricityPaint.setAntiAlias(true);
		electricityPaint.setStyle(Paint.Style.FILL);
		electricityPaint.setTypeface(Typeface.DEFAULT_BOLD);
		electricityPaint.setTextSize(showTextSize);
		electricityPaint.setColor(showTextColor);
		electricityPaint.getTextBounds(showElectricityString, 0,
				showElectricityString.length(), displayRestRect);		

		if(displayRestRect.width()>bitmapW - numberWidth/3){
			bitmapW = (int)(displayRestRect.width()+left + numberWidth/3);
		}

		Rect showRect = new Rect();			
		showRect.left = showX;
		showRect.right = showRect.left + bitmapW;
		showRect.top = showY - bitmapH/2;
		showRect.bottom = showRect.top + bitmapH;
		int showOffset = bitmapW*9/136;
		if(showRect.right > getScrollX()+displayWidth){
			showRect.right = (startDrawX + showItem * xScale)- numberWidth/3;
			showRect.left = showRect.right - bitmapW;
			showX = showRect.left;
			drawNinepath(canvas,
					R.drawable.y335_bg_history_electricity_show_right,
					showRect);
			showOffset = -showOffset;
		}
		else{
			drawNinepath(canvas,
					R.drawable.y335_bg_history_electricity_show_left,
					showRect);
		}
		canvas.drawText(
				showElectricityString,
				(showX+ (showOffset+bitmapW - displayRestRect.width())/2) * 1.0f,
				(showY+(displayRestRect.height())/4)
						* 1.0f, electricityPaint);

		/*bitmap = BitmapFactory.decodeResource(getResources(),
				R.drawable.socket_electric_toast_bg);
		bitmapW = bitmap.getWidth();
		bitmapH = bitmap.getHeight();
		
		 * if (isLongClickMove) { int toastX = getScrollX() + (displayWidth -
		 * numberWidth - bitmapW) / 2; int toastY = top - bitmapH / 2;
		 * canvas.drawBitmap(bitmap, toastX, toastY, null);
		 * electricityPaint.setTextSize(popTextSize);
		 * electricityPaint.setColor(popTextColor); showElectricityString =
		 * String.valueOf(showItem + 1);
		 * electricityPaint.getTextBounds(showElectricityString, 0,
		 * showElectricityString.length(), displayRestRect);
		 * canvas.drawText(showElectricityString, toastX + (bitmapW -
		 * displayRestRect.width()) / 2, toastY + bitmapH -
		 * displayRestRect.height() / 2, electricityPaint); }
		 */
		canvas.restore();
	}

	private void calcuteDisplayRegion(Canvas canvas) {
		displayWidth = canvas.getWidth();
		displayHeight = canvas.getHeight();
		int offset = 0;
		setShowItem(Math.min(getShowItem(), viewShowData.size() - 1));
		if (yCount == 0) {
			yCount = dayLineElectricity;
		}

		textPaint.setStrokeWidth(1f);
		textPaint.setTextSize(labelTextSize);
		textPaint.setStyle(Paint.Style.STROKE);
		if (isInteger) {
			float current = this.maxData / yCount / unitY;
			if (current - (int) current > 0.5f) {
				current = (int) current + 1;
			} else {
				current = (int) current + 0.5f;
			}
			this.unitY *= current;
			String dd = String.valueOf(this.unitY);
			textPaint.getTextBounds(dd, 0, dd.length(), rect);
		} else {
			float current = this.maxData / yCount;
			if (current - (int) current > 0.5f) {
				this.unitY = (int) current + 1;
			} else {
				this.unitY = (int) current + 0.5f;
			}
			String dd = "8.88";
			textPaint.getTextBounds(dd, 0, dd.length(), rect);
		}

		numberWidth = (int) rect.width();
		numberHeight = (int) rect.height();
		yLabelOffset = numberWidth / 3;

		displayWidth = displayWidth - left - right;
		if (getViewStyle() != VIEW_STYLE_BAR) {
			if (xCount <= 1) {
				xCount = styleXCount;
			}
			if (isShowY) {
				xScale = (displayWidth - numberWidth) / (styleXCount - 1);
			} else {
				xScale = displayWidth / (styleXCount - 1);
			}
		}
		yScale = (displayHeight - bottom - top * 2) / yCount;
		rect.left = left + getScrollX();
		rect.right = rect.left + displayWidth;
		rect.top = displayHeight - bottom;
		rect.bottom = displayHeight;
	}

	/*public float getMaxYData() {
		return maxData;
	}*/

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if (isFirstDraw) {
			calcuteDisplayRegion(canvas);
		}
		drawUnitString(canvas);
		// 画y轴文本以及横线
		if (isShowY) {
			drawYLabel(canvas);
		}
		if (this.viewStyle == VIEW_STYLE_BAR) {
			drawBarXLabel(canvas);
		} else {
			drawXLabel(canvas);
			if (viewShowData.size() > 0) {
				drawData(canvas);
			}
		}
		if (isFirstDraw) {
			isFirstDraw = false;
			if (getShowItem() >= xCount) {
				scrollTo((int) (xScale * (getShowItem() - xCount + 1)),
						getScrollY());
			} else if (getScrollX() > 0) {
				scrollTo(0, getScrollY());
			}
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			startDownX = (int) event.getX();
			startDownY = (int) event.getY();
			break;
		case MotionEvent.ACTION_MOVE:
			int currentX = (int) event.getX();
			int currentY = (int) event.getY();
			if (!isLongClickMove && Math.abs(currentX - startDownX) > 10) {
				isLongClickMove = true;
			} else {
				isLongClickMove = false;
			}
			if (isLongClickMove) {
				int sX = getScrollX() + (startDownX - currentX);
				if (sX < 0)
					sX = 0;
				else if (sX > (viewShowData.size() - xCount) * xScale) {
					sX = (int) (viewShowData.size() - xCount) * xScale;
				}
				int sY = getScrollY() + (startDownY - currentY);
				if (sY > 0) {
					sY = 0;
				} else if (sY < displayHeight - bottom - top - yCount * yScale) {
					sY = (int) (displayHeight - bottom - top - yCount * yScale);
				}
				if (viewShowData.size() > xCount)
					this.scrollTo(sX, getScrollY());
				// if(this.getScrollX())
				startDownX = currentX;
				startDownY = currentY;
			}
			break;
		case MotionEvent.ACTION_UP:
			startDownX = (int) event.getX() + getScrollX();
			startDownY = (int) event.getY();

			rect.left = left + getScrollX();
			rect.right = rect.left + displayWidth;
			rect.top = displayHeight - bottom;
			rect.bottom = displayHeight;
			if (rect.contains(startDownX, startDownY) && !isLongClickMove) {
				showItem = (startDownX - numberWidth) / xScale;
				showItem = Math.min(Math.max(showItem, 0),
						viewShowData.size() - 1);
				L.d("showItem=" + showItem);
				setShowItemValue(xArrayList.get(showItem));
				invalidate();
			} else if (isLongClickMove) {
				isLongClickMove = false;
			}
			break;
		}
		return true;// 处理了触摸消息，消息不再传递
	}

	public void addViewShowData(String xContent, float yContent) {
		this.maxData = Math.max(maxData, Float.valueOf(yContent));
		this.viewShowData.put(xContent, yContent);
	}

	public void clearViewShowData() {
		this.viewShowData.clear();
	}

	public boolean isFirstDraw() {
		return isFirstDraw;
	}

	public void setFirstDraw(boolean isFirstDraw) {
		this.isFirstDraw = isFirstDraw;
	}

	public float getDrawLineSize() {
		return drawLineSize;
	}

	public void setDrawLineSize(float drawLineSize) {
		this.drawLineSize = drawLineSize;
	}

}