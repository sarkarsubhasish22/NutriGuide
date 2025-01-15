package org.apache.poi.hpsf;

import java.util.Map;
import org.apache.poi.hpsf.wellknown.PropertyIDMap;
import org.apache.poi.hpsf.wellknown.SectionIDMap;

public class DocumentSummaryInformation extends SpecialPropertySet {
    public static final String DEFAULT_STREAM_NAME = "\u0005DocumentSummaryInformation";

    public PropertyIDMap getPropertySetIDMap() {
        return PropertyIDMap.getDocumentSummaryInformationProperties();
    }

    public DocumentSummaryInformation(PropertySet ps) throws UnexpectedPropertySetTypeException {
        super(ps);
        if (!isDocumentSummaryInformation()) {
            throw new UnexpectedPropertySetTypeException("Not a " + getClass().getName());
        }
    }

    public String getCategory() {
        return (String) getProperty(2);
    }

    public void setCategory(String category) {
        ((MutableSection) getFirstSection()).setProperty(2, category);
    }

    public void removeCategory() {
        ((MutableSection) getFirstSection()).removeProperty(2);
    }

    public String getPresentationFormat() {
        return (String) getProperty(3);
    }

    public void setPresentationFormat(String presentationFormat) {
        ((MutableSection) getFirstSection()).setProperty(3, presentationFormat);
    }

    public void removePresentationFormat() {
        ((MutableSection) getFirstSection()).removeProperty(3);
    }

    public int getByteCount() {
        return getPropertyIntValue(4);
    }

    public void setByteCount(int byteCount) {
        ((MutableSection) getFirstSection()).setProperty(4, byteCount);
    }

    public void removeByteCount() {
        ((MutableSection) getFirstSection()).removeProperty(4);
    }

    public int getLineCount() {
        return getPropertyIntValue(5);
    }

    public void setLineCount(int lineCount) {
        ((MutableSection) getFirstSection()).setProperty(5, lineCount);
    }

    public void removeLineCount() {
        ((MutableSection) getFirstSection()).removeProperty(5);
    }

    public int getParCount() {
        return getPropertyIntValue(6);
    }

    public void setParCount(int parCount) {
        ((MutableSection) getFirstSection()).setProperty(6, parCount);
    }

    public void removeParCount() {
        ((MutableSection) getFirstSection()).removeProperty(6);
    }

    public int getSlideCount() {
        return getPropertyIntValue(7);
    }

    public void setSlideCount(int slideCount) {
        ((MutableSection) getFirstSection()).setProperty(7, slideCount);
    }

    public void removeSlideCount() {
        ((MutableSection) getFirstSection()).removeProperty(7);
    }

    public int getNoteCount() {
        return getPropertyIntValue(8);
    }

    public void setNoteCount(int noteCount) {
        ((MutableSection) getFirstSection()).setProperty(8, noteCount);
    }

    public void removeNoteCount() {
        ((MutableSection) getFirstSection()).removeProperty(8);
    }

    public int getHiddenCount() {
        return getPropertyIntValue(9);
    }

    public void setHiddenCount(int hiddenCount) {
        ((MutableSection) getSections().get(0)).setProperty(9, hiddenCount);
    }

    public void removeHiddenCount() {
        ((MutableSection) getFirstSection()).removeProperty(9);
    }

    public int getMMClipCount() {
        return getPropertyIntValue(10);
    }

    public void setMMClipCount(int mmClipCount) {
        ((MutableSection) getFirstSection()).setProperty(10, mmClipCount);
    }

    public void removeMMClipCount() {
        ((MutableSection) getFirstSection()).removeProperty(10);
    }

    public boolean getScale() {
        return getPropertyBooleanValue(11);
    }

    public void setScale(boolean scale) {
        ((MutableSection) getFirstSection()).setProperty(11, scale);
    }

    public void removeScale() {
        ((MutableSection) getFirstSection()).removeProperty(11);
    }

    public byte[] getHeadingPair() {
        notYetImplemented("Reading byte arrays ");
        return (byte[]) getProperty(12);
    }

    public void setHeadingPair(byte[] headingPair) {
        notYetImplemented("Writing byte arrays ");
    }

    public void removeHeadingPair() {
        ((MutableSection) getFirstSection()).removeProperty(12);
    }

    public byte[] getDocparts() {
        notYetImplemented("Reading byte arrays");
        return (byte[]) getProperty(13);
    }

    public void setDocparts(byte[] docparts) {
        notYetImplemented("Writing byte arrays");
    }

    public void removeDocparts() {
        ((MutableSection) getFirstSection()).removeProperty(13);
    }

    public String getManager() {
        return (String) getProperty(14);
    }

    public void setManager(String manager) {
        ((MutableSection) getFirstSection()).setProperty(14, manager);
    }

    public void removeManager() {
        ((MutableSection) getFirstSection()).removeProperty(14);
    }

    public String getCompany() {
        return (String) getProperty(15);
    }

    public void setCompany(String company) {
        ((MutableSection) getFirstSection()).setProperty(15, company);
    }

    public void removeCompany() {
        ((MutableSection) getFirstSection()).removeProperty(15);
    }

    public boolean getLinksDirty() {
        return getPropertyBooleanValue(16);
    }

    public void setLinksDirty(boolean linksDirty) {
        ((MutableSection) getFirstSection()).setProperty(16, linksDirty);
    }

    public void removeLinksDirty() {
        ((MutableSection) getFirstSection()).removeProperty(16);
    }

    public CustomProperties getCustomProperties() {
        CustomProperties cps = null;
        if (getSectionCount() >= 2) {
            cps = new CustomProperties();
            Section section = (Section) getSections().get(1);
            Map<Long, String> dictionary = section.getDictionary();
            Property[] properties = section.getProperties();
            int propertyCount = 0;
            for (Property p : properties) {
                long id = p.getID();
                if (!(id == 0 || id == 1)) {
                    propertyCount++;
                    CustomProperty cp = new CustomProperty(p, dictionary.get(Long.valueOf(id)));
                    cps.put(cp.getName(), cp);
                }
            }
            if (cps.size() != propertyCount) {
                cps.setPure(false);
            }
        }
        return cps;
    }

    public void setCustomProperties(CustomProperties customProperties) {
        ensureSection2();
        MutableSection section = (MutableSection) getSections().get(1);
        Map<Long, String> dictionary = customProperties.getDictionary();
        section.clear();
        int cpCodepage = customProperties.getCodepage();
        if (cpCodepage < 0) {
            cpCodepage = section.getCodepage();
        }
        if (cpCodepage < 0) {
            cpCodepage = 1200;
        }
        customProperties.setCodepage(cpCodepage);
        section.setCodepage(cpCodepage);
        section.setDictionary(dictionary);
        for (CustomProperty p : customProperties.values()) {
            section.setProperty(p);
        }
    }

    private void ensureSection2() {
        if (getSectionCount() < 2) {
            MutableSection s2 = new MutableSection();
            s2.setFormatID(SectionIDMap.DOCUMENT_SUMMARY_INFORMATION_ID[1]);
            addSection(s2);
        }
    }

    public void removeCustomProperties() {
        if (getSectionCount() >= 2) {
            getSections().remove(1);
            return;
        }
        throw new HPSFRuntimeException("Illegal internal format of Document SummaryInformation stream: second section is missing.");
    }

    private void notYetImplemented(String msg) {
        throw new UnsupportedOperationException(msg + " is not yet implemented.");
    }
}
