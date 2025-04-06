import java.util.ArrayList;
import java.util.List;

public class Presentation {
	private String title;               // title of the presentation
	private List<Slide> slides;         // list of slides
	private int currentSlideNumber;     // the current slide number
	private List<Observer> observers;   // list of observers
	private SlideViewerComponent showView; // the view component

	public Presentation(String title) {
		this.title = title;
		this.slides = new ArrayList<>();
		this.currentSlideNumber = 0;
		this.observers = new ArrayList<>();
	}

	public void setShowView(SlideViewerComponent view) {
		this.showView = view;
	}

	public SlideViewerComponent getShowView() {
		return showView;
	}

	// Observer management
	public void addObserver(Observer observer) {
		if (!observers.contains(observer)) {
			observers.add(observer);
		}
	}

	public void removeObserver(Observer observer) {
		observers.remove(observer);
	}

	public void notifyObservers() {
		for (Observer o : observers) {
			o.update();
		}
	}

	// Presentation methods
	public int getSize() {
		return slides.size();
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public int getSlideNumber() {
		return currentSlideNumber;
	}

	public void setSlideNumber(int number) {
		if (number >= 0 && number < getSize()) {
			currentSlideNumber = number;
			notifyObservers();
		}
	}

	public void prevSlide() {
		if (currentSlideNumber > 0) {
			currentSlideNumber--;
			notifyObservers();
		}
	}

	public void nextSlide() {
		if (currentSlideNumber < slides.size() - 1) {
			currentSlideNumber++;
			notifyObservers();
		}
	}

	/**
	 * Checks if we're currently on the first slide.
	 * @return true if we're at the first slide, false otherwise
	 */
	public boolean isFirstSlide() {
		return currentSlideNumber == 0;
	}

	/**
	 * Checks if we're currently on the last slide.
	 * @return true if we're at the last slide, false otherwise
	 */
	public boolean isLastSlide() {
		return slides.size() > 0 && currentSlideNumber == slides.size() - 1;
	}

	public void clear() {
		slides = new ArrayList<>();
		currentSlideNumber = 0;
		notifyObservers();
	}

	public void addSlide(Slide slide) {
		slides.add(slide);
	}

	public Slide getSlide(int number) {
		if (number < 0 || number >= getSize()) {
			throw new IndexOutOfBoundsException("Invalid slide number " + number);
		}
		return slides.get(number);
	}

	public Slide getCurrentSlide() {
		return (getSlideNumber() >= 0 && getSlideNumber() < getSize()) ?
				getSlide(currentSlideNumber) : null;
	}

	public void exit(int statusNumber) {
		System.exit(statusNumber);
	}
}