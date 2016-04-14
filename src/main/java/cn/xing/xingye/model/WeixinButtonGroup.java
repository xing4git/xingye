package cn.xing.xingye.model;

import com.google.common.collect.Lists;

import java.util.List;

/**
 * Created by indexing on 16/4/14.
 */
public class WeixinButtonGroup extends WeixinButton {
    private List<WeixinButton> sub_button = Lists.newArrayList();

    public WeixinButtonGroup() {
    }

    public WeixinButtonGroup(String name) {
        super(name);
    }

    public List<WeixinButton> getSub_button() {
        return sub_button;
    }

    public void addButtons(List<WeixinButton> buttons) {
        this.sub_button.addAll(buttons);
    }

    public void addButton(WeixinButton button) {
        this.sub_button.add(button);
    }
}
