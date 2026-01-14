import java.util.HashMap;
import java.util.Random;

public class LanguageModel {

    // The map of this model.
    // Maps windows to lists of charachter data objects.
    HashMap<String, List> CharDataMap;
    
    // The window length used in this model.
    int windowLength;
    
    // The random number generator used by this model. 
	private Random randomGenerator;

    /** Constructs a language model with the given window length and a given
     *  seed value. Generating texts from this model multiple times with the 
     *  same seed value will produce the same random texts. Good for debugging. */
    public LanguageModel(int windowLength, int seed) {
        this.windowLength = windowLength;
        randomGenerator = new Random(seed);
        CharDataMap = new HashMap<String, List>();
    }

    /** Constructs a language model with the given window length.
     * Generating texts from this model multiple times will produce
     * different random texts. Good for production. */
    public LanguageModel(int windowLength) {
        this.windowLength = windowLength;
        randomGenerator = new Random();
        CharDataMap = new HashMap<String, List>();
    }

    /** Builds a language model from the text in the given file (the corpus). */
	public void train(String fileName) {
        String window = "";
        char c;
		In in = new In(fileName);
        for (int i = 0; i < windowLength; i++) {
            window += in.readChar();
        }
        // Processes the entire text, one character at a time
        while (!in.isEmpty()) {
        // Gets the next character
            c = in.readChar();
        // Checks if the window is already in the map
            List probs = CharDataMap.get(window);
            if (probs == null){
                probs = new List();
                CharDataMap.put(window, probs);
            }
            probs.update(c);
            // Advances the window: adds c to the window’s end, and deletes the
            // window's first character.
            window = window.substring(1) + c;
        }
        for (List probs : CharDataMap.values())
            calculateProbabilities(probs);
    }
	

    // Computes and sets the probabilities (p and cp fields) of all the
	// characters in the given list. */
	void calculateProbabilities(List probs) {				
		int counter = 0;
        for (int i = 0; i < probs.getSize(); i++){
            counter += probs.get(i).count;
        }
        double sum = 0.0;
         for (int i = 0; i < probs.getSize(); i++){
            CharData cd = probs.get(i);
            cd.p = (double) cd.count / counter;
            sum += cd.p;
            cd.cp = sum;
         }
	}

    // Returns a random character from the given probabilities list.
	char getRandomChar(List probs) {
		double r = randomGenerator.nextDouble();
        ListIterator itr = probs.listIterator(0);
        while (itr.hasNext()) {
            if (r <= itr.current.cp.cp) return itr.current.cp.chr;
            itr.next();
        }
		return ' ';
	}

    /**
	 * Generates a random text, based on the probabilities that were learned during training. 
	 * @param initialText - text to start with. If initialText's last substring of size numberOfLetters
	 * doesn't appear as a key in Map, we generate no text and return only the initial text. 
	 * @param numberOfLetters - the size of text to generate
	 * @return the generated text
	 */
	public String generate(String initialText, int textLength) {
		if (initialText.length() < windowLength) return initialText;
        String window = initialText.substring(initialText.length() - windowLength);
        if (CharDataMap.get(initialText) == null) return initialText;
        String ret = initialText;
        for (int i = 0; i < textLength; i++) {
            char ch = getRandomChar(CharDataMap.get(window));
            ret += ch;
            window = window.substring(1) + ch;
        }
        return ret;
    }

    /** Returns a string representing the map of this language model. */
	public String toString() {
		StringBuilder str = new StringBuilder();
		for (String key : CharDataMap.keySet()) {
			List keyProbs = CharDataMap.get(key);
			str.append(key + " : " + keyProbs + "\n");
		}
		return str.toString();
	}

    public static void main(String[] args) {
    }
}
