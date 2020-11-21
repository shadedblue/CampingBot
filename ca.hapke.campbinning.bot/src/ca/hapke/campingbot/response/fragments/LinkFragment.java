package ca.hapke.campingbot.response.fragments;

import ca.hapke.campingbot.processors.MessageProcessor;

/**
 * @author Nathan Hapke
 */
public class LinkFragment extends ResultFragment {

	private String url, text;

	public LinkFragment(String url, String text) {
		this.url = url;
		this.text = text;
	}

	@Override
	public String getValue(MessageProcessor processor, boolean useMarkupV2) {
		return "[" + processor.processString(text, useMarkupV2) + "](" + url + ")";
	}

}
