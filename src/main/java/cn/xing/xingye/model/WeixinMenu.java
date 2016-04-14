package cn.xing.xingye.model;

import com.google.common.collect.Lists;

import java.util.List;

/**
 * Created by indexing on 16/4/14.
 */
public class WeixinMenu {
    private List<WeixinButton> button = Lists.newArrayList();

    public List<WeixinButton> getButton() {
        return button;
    }

    public void addButtons(List<WeixinButton> buttons) {
        this.button.addAll(buttons);
    }

    public void addButton(WeixinButton b) {
        this.button.add(b);
    }
}
