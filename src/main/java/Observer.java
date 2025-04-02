/**
 * This interface is part of the Observer pattern.
 * When you implement this interface, your class will get notified when something changes
 * in the object it's watching. Think of it like signing up for notifications.
 */
public interface Observer {
    /**
     * This method gets called whenever the thing you're watching changes.
     * It's like getting a notification on your phone.
     */
    void update();
}
