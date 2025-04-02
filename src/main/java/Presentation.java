import java.util.ArrayList;
import java.util.List;

/**
 * This class represents a complete slide presentation.
 * It contains all the slides and keeps track of which slide we're currently on.
 * It also tells its observers when something changes, so they can update themselves.
 */
public class Presentation {
    private final List<Observer> observers;   // list of observers
    private String title;               // title of the presentation
    private List<Slide> slides;         // list of slides
    private int currentSlideNumber;     // the current slide number
    private SlideViewerComponent showView; // the view component

    /**
     * We create a new presentation with the given title.
     * It starts empty, with no slides yet.
     */
    public Presentation(String title) {
        this.title = title;
        this.slides = new ArrayList<>();
        this.currentSlideNumber = 0;
        this.observers = new ArrayList<>();
    }

    /**
     * This gives us the component that's showing the slides.
     */
    public SlideViewerComponent getShowView() {
        return showView;
    }

    /**
     * This connects the presentation to the component that shows the slides.
     */
    public void setShowView(SlideViewerComponent view) {
        this.showView = view;
    }

    // Observer management

    /**
     * This lets a new observer sign up for updates.
     * The observer will be notified whenever something changes in the presentation.
     */
    public void addObserver(Observer observer) {
        if (!observers.contains(observer)) {
            observers.add(observer);
        }
    }

    /**
     * This removes an observer from the notification list.
     * They won't get any more updates after this.
     */
    public void removeObserver(Observer observer) {
        observers.remove(observer);
    }

    /**
     * This tells all observers that something has changed.
     * They should update themselves to show the current state.
     */
    public void notifyObservers() {
        for (Observer o : observers) {
            o.update();
        }
    }

    // Presentation methods

    /**
     * This tells us how many slides are in the presentation.
     */
    public int getSize() {
        return slides.size();
    }

    /**
     * This gives us the title of the presentation.
     */
    public String getTitle() {
        return title;
    }

    /**
     * This changes the title of the presentation.
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * This tells us which slide number we're currently on.
     */
    public int getSlideNumber() {
        return currentSlideNumber;
    }

    /**
     * This jumps to a specific slide number.
     * If the number is valid, we change to that slide and notify everyone.
     */
    public void setSlideNumber(int number) {
        if (number >= 0 && number < getSize()) {
            currentSlideNumber = number;
            notifyObservers();
        }
    }

    /**
     * This moves to the previous slide.
     * If we're already at the first slide, nothing happens.
     */
    public void prevSlide() {
        System.out.println("Presentation.prevSlide(): current=" + (currentSlideNumber + 1) +
                ", condition=" + (currentSlideNumber > 0));

        if (currentSlideNumber > 0) {
            currentSlideNumber--;
            System.out.println("  -> Moving to slide " + (currentSlideNumber + 1));
            notifyObservers();
        } else {
            System.out.println("  -> Already at first slide, can't move back");
        }
    }

    /**
     * This moves to the next slide.
     * If we're already at the last slide, nothing happens.
     */
    public void nextSlide() {
        System.out.println("Presentation.nextSlide(): current=" + (currentSlideNumber + 1) +
                ", size=" + slides.size() +
                ", condition=" + (currentSlideNumber < slides.size() - 1));

        // Check if we're not already at the last slide
        if (currentSlideNumber < slides.size() - 1) {
            currentSlideNumber++;
            System.out.println("  -> Moving to slide " + (currentSlideNumber + 1));
            notifyObservers();
        } else {
            System.out.println("  -> Already at last slide, can't move further");
        }
    }

    /**
     * This removes all slides and resets the presentation.
     */
    public void clear() {
        slides = new ArrayList<>();
        currentSlideNumber = 0;
        notifyObservers();
    }

    /**
     * This adds a new slide to the presentation.
     */
    public void addSlide(Slide slide) {
        slides.add(slide);
    }

    /**
     * This gives us a specific slide by its number.
     * If the number is invalid, it throws an error.
     */
    public Slide getSlide(int number) {
        if (number < 0 || number >= getSize()) {
            throw new IndexOutOfBoundsException("Invalid slide number " + number);
        }
        return slides.get(number);
    }

    /**
     * This gives us the slide we're currently on.
     * If there's no valid current slide, it returns null.
     */
    public Slide getCurrentSlide() {
        return (getSlideNumber() >= 0 && getSlideNumber() < getSize()) ?
                getSlide(currentSlideNumber) : null;
    }

    /**
     * This closes the application with the given status code.
     */
    public void exit(int statusNumber) {
        System.exit(statusNumber);
    }

    /**
     * This tells us if we're currently on the last slide.
     * Handy for debugging navigation issues.
     */
    public boolean isLastSlide() {
        return currentSlideNumber == slides.size() - 1;
    }

    /**
     * This tells us if we're currently on the first slide.
     * Handy for debugging navigation issues.
     */
    public boolean isFirstSlide() {
        return currentSlideNumber == 0;
    }
}