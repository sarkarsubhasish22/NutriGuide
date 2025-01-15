package org.apache.poi.hssf.record;

import org.apache.poi.util.BitField;
import org.apache.poi.util.BitFieldFactory;
import org.apache.poi.util.LittleEndianOutput;

public final class WindowOneRecord extends StandardRecord {
    private static final BitField hidden = BitFieldFactory.getInstance(1);
    private static final BitField hscroll = BitFieldFactory.getInstance(8);
    private static final BitField iconic = BitFieldFactory.getInstance(2);
    private static final BitField reserved = BitFieldFactory.getInstance(4);
    public static final short sid = 61;
    private static final BitField tabs = BitFieldFactory.getInstance(32);
    private static final BitField vscroll = BitFieldFactory.getInstance(16);
    private short field_1_h_hold;
    private short field_2_v_hold;
    private short field_3_width;
    private short field_4_height;
    private short field_5_options;
    private int field_6_active_sheet;
    private int field_7_first_visible_tab;
    private short field_8_num_selected_tabs;
    private short field_9_tab_width_ratio;

    public WindowOneRecord() {
    }

    public WindowOneRecord(RecordInputStream in) {
        this.field_1_h_hold = in.readShort();
        this.field_2_v_hold = in.readShort();
        this.field_3_width = in.readShort();
        this.field_4_height = in.readShort();
        this.field_5_options = in.readShort();
        this.field_6_active_sheet = in.readShort();
        this.field_7_first_visible_tab = in.readShort();
        this.field_8_num_selected_tabs = in.readShort();
        this.field_9_tab_width_ratio = in.readShort();
    }

    public void setHorizontalHold(short h) {
        this.field_1_h_hold = h;
    }

    public void setVerticalHold(short v) {
        this.field_2_v_hold = v;
    }

    public void setWidth(short w) {
        this.field_3_width = w;
    }

    public void setHeight(short h) {
        this.field_4_height = h;
    }

    public void setOptions(short o) {
        this.field_5_options = o;
    }

    public void setHidden(boolean ishidden) {
        this.field_5_options = hidden.setShortBoolean(this.field_5_options, ishidden);
    }

    public void setIconic(boolean isiconic) {
        this.field_5_options = iconic.setShortBoolean(this.field_5_options, isiconic);
    }

    public void setDisplayHorizonalScrollbar(boolean scroll) {
        this.field_5_options = hscroll.setShortBoolean(this.field_5_options, scroll);
    }

    public void setDisplayVerticalScrollbar(boolean scroll) {
        this.field_5_options = vscroll.setShortBoolean(this.field_5_options, scroll);
    }

    public void setDisplayTabs(boolean disptabs) {
        this.field_5_options = tabs.setShortBoolean(this.field_5_options, disptabs);
    }

    public void setActiveSheetIndex(int index) {
        this.field_6_active_sheet = index;
    }

    public void setSelectedTab(short s) {
        setActiveSheetIndex(s);
    }

    public void setFirstVisibleTab(int t) {
        this.field_7_first_visible_tab = t;
    }

    public void setDisplayedTab(short t) {
        setFirstVisibleTab(t);
    }

    public void setNumSelectedTabs(short n) {
        this.field_8_num_selected_tabs = n;
    }

    public void setTabWidthRatio(short r) {
        this.field_9_tab_width_ratio = r;
    }

    public short getHorizontalHold() {
        return this.field_1_h_hold;
    }

    public short getVerticalHold() {
        return this.field_2_v_hold;
    }

    public short getWidth() {
        return this.field_3_width;
    }

    public short getHeight() {
        return this.field_4_height;
    }

    public short getOptions() {
        return this.field_5_options;
    }

    public boolean getHidden() {
        return hidden.isSet(this.field_5_options);
    }

    public boolean getIconic() {
        return iconic.isSet(this.field_5_options);
    }

    public boolean getDisplayHorizontalScrollbar() {
        return hscroll.isSet(this.field_5_options);
    }

    public boolean getDisplayVerticalScrollbar() {
        return vscroll.isSet(this.field_5_options);
    }

    public boolean getDisplayTabs() {
        return tabs.isSet(this.field_5_options);
    }

    public int getActiveSheetIndex() {
        return this.field_6_active_sheet;
    }

    public short getSelectedTab() {
        return (short) getActiveSheetIndex();
    }

    public int getFirstVisibleTab() {
        return this.field_7_first_visible_tab;
    }

    public short getDisplayedTab() {
        return (short) getFirstVisibleTab();
    }

    public short getNumSelectedTabs() {
        return this.field_8_num_selected_tabs;
    }

    public short getTabWidthRatio() {
        return this.field_9_tab_width_ratio;
    }

    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("[WINDOW1]\n");
        buffer.append("    .h_hold          = ");
        buffer.append(Integer.toHexString(getHorizontalHold()));
        buffer.append("\n");
        buffer.append("    .v_hold          = ");
        buffer.append(Integer.toHexString(getVerticalHold()));
        buffer.append("\n");
        buffer.append("    .width           = ");
        buffer.append(Integer.toHexString(getWidth()));
        buffer.append("\n");
        buffer.append("    .height          = ");
        buffer.append(Integer.toHexString(getHeight()));
        buffer.append("\n");
        buffer.append("    .options         = ");
        buffer.append(Integer.toHexString(getOptions()));
        buffer.append("\n");
        buffer.append("        .hidden      = ");
        buffer.append(getHidden());
        buffer.append("\n");
        buffer.append("        .iconic      = ");
        buffer.append(getIconic());
        buffer.append("\n");
        buffer.append("        .hscroll     = ");
        buffer.append(getDisplayHorizontalScrollbar());
        buffer.append("\n");
        buffer.append("        .vscroll     = ");
        buffer.append(getDisplayVerticalScrollbar());
        buffer.append("\n");
        buffer.append("        .tabs        = ");
        buffer.append(getDisplayTabs());
        buffer.append("\n");
        buffer.append("    .activeSheet     = ");
        buffer.append(Integer.toHexString(getActiveSheetIndex()));
        buffer.append("\n");
        buffer.append("    .firstVisibleTab    = ");
        buffer.append(Integer.toHexString(getFirstVisibleTab()));
        buffer.append("\n");
        buffer.append("    .numselectedtabs = ");
        buffer.append(Integer.toHexString(getNumSelectedTabs()));
        buffer.append("\n");
        buffer.append("    .tabwidthratio   = ");
        buffer.append(Integer.toHexString(getTabWidthRatio()));
        buffer.append("\n");
        buffer.append("[/WINDOW1]\n");
        return buffer.toString();
    }

    public void serialize(LittleEndianOutput out) {
        out.writeShort(getHorizontalHold());
        out.writeShort(getVerticalHold());
        out.writeShort(getWidth());
        out.writeShort(getHeight());
        out.writeShort(getOptions());
        out.writeShort(getActiveSheetIndex());
        out.writeShort(getFirstVisibleTab());
        out.writeShort(getNumSelectedTabs());
        out.writeShort(getTabWidthRatio());
    }

    /* access modifiers changed from: protected */
    public int getDataSize() {
        return 18;
    }

    public short getSid() {
        return 61;
    }
}
