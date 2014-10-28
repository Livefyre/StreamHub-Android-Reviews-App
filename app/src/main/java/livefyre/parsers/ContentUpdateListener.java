package livefyre.parsers;

import java.util.HashSet;

public interface ContentUpdateListener {
	void onDataUpdate(HashSet<String> updates);
}
