package com.zafnks.utils;

public enum ArticalType {

	XUANHUAN("玄幻"), XIUZHEN("修真"), DUSHI("都市"), LISHI("历史"), JUNSHI("军事"), WANGYOU(
			"网游"), KEHUAN("科幻"), QITA("其他");

	private final String chn;

	private ArticalType(String chn) {
		this.chn = chn;
	}

	public static ArticalType getType(String chn) {
		switch (chn) {
		case "玄幻":
			return ArticalType.XUANHUAN;
		case "修真":
			return ArticalType.XIUZHEN;
		case "都市":
			return ArticalType.DUSHI;
		case "历史":
			return ArticalType.LISHI;
		case "军事":
			return ArticalType.JUNSHI;
		case "网游":
			return ArticalType.WANGYOU;
		case "科幻":
			return ArticalType.KEHUAN;
		case "其他":
			return ArticalType.QITA;
		default:
			return null;
		}
	}

	@Override
	public String toString() {
		return chn;
	}
}
