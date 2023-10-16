package no.shhsoft.data;

import java.util.HashSet;
import java.util.Set;

/**
 * @author <a href="mailto:shh@thathost.com">Sverre H. Huseby</a>
 */
public class StateChangeListenerManager<T> {

    private final Set<StateChangeListener<T>> listeners = new HashSet<>();

    public final void addListener(final StateChangeListener<T> listener) {
        synchronized (listeners) {
            listeners.add(listener);
        }
    }

    public final void removeListener(final StateChangeListener<T> listener) {
        synchronized (listeners) {
            listeners.remove(listener);
        }
    }

    public final void removeListeners() {
        synchronized (listeners) {
            listeners.clear();
        }
    }

    public final void notifyChanged(final T t) {
        synchronized (listeners) {
            for (final StateChangeListener<T> listener : listeners) {
                listener.stateChanged(t);
            }
        }
    }

}
