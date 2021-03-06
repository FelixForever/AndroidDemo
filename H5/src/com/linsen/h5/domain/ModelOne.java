package com.linsen.h5.domain;

/**
 * 模版1 一张图片，一个文字
 * 
 * @author linsen
 * 
 */
public class ModelOne {
	private String imageUrl;// 上传图片时后台返回的，
	private String imagePath;// 手机本地存储的路径
	private float rotate;// 旋转加度
	private float scale;// 缩放大小
	private float transX;// 水平位移
	private float transY;// 垂直位移
	private String text;// 文字内容
	private int textSize;// 文字大小
	private int textColor;// 文字颜色

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public String getImagePath() {
		return imagePath;
	}

	public void setImagePath(String imagePath) {
		this.imagePath = imagePath;
	}

	public float getRotate() {
		return rotate;
	}

	public void setRotate(float rotate) {
		this.rotate = rotate;
	}

	public float getScale() {
		return scale;
	}

	public void setScale(float scale) {
		this.scale = scale;
	}

	public float getTransX() {
		return transX;
	}

	public void setTransX(float transX) {
		this.transX = transX;
	}

	public float getTransY() {
		return transY;
	}

	public void setTransY(float transY) {
		this.transY = transY;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public int getTextSize() {
		return textSize;
	}

	public void setTextSize(int textSize) {
		this.textSize = textSize;
	}

	public int getTextColor() {
		return textColor;
	}

	public void setTextColor(int textColor) {
		this.textColor = textColor;
	}

}
