package org.apache.poi.poifs.eventfilesystem;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import org.apache.poi.poifs.filesystem.DocumentDescriptor;
import org.apache.poi.poifs.filesystem.POIFSDocumentPath;

class POIFSReaderRegistry {
    private Map chosenDocumentDescriptors = new HashMap();
    private Set omnivorousListeners = new HashSet();
    private Map selectiveListeners = new HashMap();

    POIFSReaderRegistry() {
    }

    /* access modifiers changed from: package-private */
    public void registerListener(POIFSReaderListener listener, POIFSDocumentPath path, String documentName) {
        if (!this.omnivorousListeners.contains(listener)) {
            Set descriptors = (Set) this.selectiveListeners.get(listener);
            if (descriptors == null) {
                descriptors = new HashSet();
                this.selectiveListeners.put(listener, descriptors);
            }
            DocumentDescriptor descriptor = new DocumentDescriptor(path, documentName);
            if (descriptors.add(descriptor)) {
                Set listeners = (Set) this.chosenDocumentDescriptors.get(descriptor);
                if (listeners == null) {
                    listeners = new HashSet();
                    this.chosenDocumentDescriptors.put(descriptor, listeners);
                }
                listeners.add(listener);
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void registerListener(POIFSReaderListener listener) {
        if (!this.omnivorousListeners.contains(listener)) {
            removeSelectiveListener(listener);
            this.omnivorousListeners.add(listener);
        }
    }

    /* access modifiers changed from: package-private */
    public Iterator getListeners(POIFSDocumentPath path, String name) {
        Set rval = new HashSet(this.omnivorousListeners);
        Set selectiveListeners2 = (Set) this.chosenDocumentDescriptors.get(new DocumentDescriptor(path, name));
        if (selectiveListeners2 != null) {
            rval.addAll(selectiveListeners2);
        }
        return rval.iterator();
    }

    private void removeSelectiveListener(POIFSReaderListener listener) {
        Set<DocumentDescriptor> selectedDescriptors = (Set) this.selectiveListeners.remove(listener);
        if (selectedDescriptors != null) {
            for (DocumentDescriptor dropDocument : selectedDescriptors) {
                dropDocument(listener, dropDocument);
            }
        }
    }

    private void dropDocument(POIFSReaderListener listener, DocumentDescriptor descriptor) {
        Set listeners = (Set) this.chosenDocumentDescriptors.get(descriptor);
        listeners.remove(listener);
        if (listeners.size() == 0) {
            this.chosenDocumentDescriptors.remove(descriptor);
        }
    }
}
