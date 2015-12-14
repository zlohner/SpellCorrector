package spell;

/** Trie
 *
 * a tree structure with 26-child nodes (letters) that represents a set of words (a dictionary)
 */
public class Trie implements ITrie {

    public static final int NUM_LETTERS = 26;

    private Node root;
    private int wordCount;
    private int nodeCount;

    public Trie() {
        this.root = new Node();
        this.wordCount = 0;
        this.nodeCount = 1;
    }

    /** add
     *
     * calls @recursiveAdd with arguments (root, word)
     *
     * @param word: The word being added to the Trie
     */
    public void add(String word){ recursiveAdd(root, word.toLowerCase()); }

    /** recursiveAdd
     *
     * recursively adds a word to the Trie
     *
     * @param curr: the current Node in the Trie
     * @param word: a substring of the word containing the remaining letters
     */
    public void recursiveAdd(Node curr, String word) {
        if (word.length() > 0) {
            Node[] nodes = curr.getNodes();
            if (nodes[word.charAt(0) - 'a'] != null) {
                recursiveAdd(nodes[word.charAt(0) - 'a'], word.substring(1));
            }
            else if (nodes[word.charAt(0) - 'a'] == null) {
                curr.getNodes()[word.charAt(0) - 'a'] = new Node();
                this.nodeCount++;
                recursiveAdd(nodes[word.charAt(0) - 'a'], word.substring(1));
            }
        }
        else if (word.length() == 0) {
            if (curr.getValue() == 0) { this.wordCount++; }
            curr.addValue();
        }
    }

    /** find
     *
     * calls @recursiveFind with arguments (root, word)
     *
     * @param word: The word to search for
     * @return: the node representing the word, or null if not found
     */
    public INode find(String word) { return recursiveFind(root, word); }

    /** recursiveFind
     *
     * recursively finds a word in the Trie
     *
     * @param curr: the current Node in the Trie
     * @param word: a substring of the word containing the remaining letters
     * @return: the node representing the word, or null if not found
     */
    public INode recursiveFind(Node curr, String word) {
        if (word.length() > 0) {
            Node nextNode = curr.getNodes()[word.charAt(0) - 'a'];
            if (nextNode != null) {
                return recursiveFind(nextNode, word.substring(1));
            }
            else if (nextNode == null) { return null; }
        }
        if (curr.getValue() > 0) { return curr; }
        else { return null; }
    }

    public int getWordCount() { return wordCount; }

    public int getNodeCount() { return nodeCount; }

    @Override
    public String toString() {
        return root.toString(); /* use Node's recursive toString */
    }

    @Override
    public int hashCode() { return (31 * wordCount) + (37 * nodeCount); }

    @Override
    public boolean equals(Object o) {
        if (o == null) { return false; }
        else if (this == o) { return true; }
        else if (this.getClass() != o.getClass()) { return false; }
        else {
            Trie oTrie = (Trie)o;
            if (this.wordCount != oTrie.getWordCount()
                    || this.nodeCount != oTrie.getNodeCount()) { return false; }
            else if (!this.root.equals(oTrie.root)) { return false; }
        }
        return true;
    }

    /** Node
     *
     * implements ITrie.Node
     *
     * a node in a Trie - each node represents a word
     *
     * @member nodes: an array of nodes representing the letters a-z
     * @member value: the number of times a word is included in the Trie, 0 if not included
     */
    public class Node implements ITrie.INode {

        private Node[] nodes;
        private int value;

        public Node() {
            this.value = 0;
            this.nodes = new Node[NUM_LETTERS];
        }

        /** toString
         *
         * calls toString (recursive)
         *
         * @return: a string containing all words reachable from the node in the form <word>\n for each word
         */
        @Override
        public String toString() {
            return toString(this, new String());
        }

        /** toString (recursive)
         *
         * returns a string representation of all words reachable from the node in alphabetical order
         *
         * @param curr: current node
         * @param prev: string representing the previously traversed nodes
         * @return: a string containing all words reachable from the node in the form <word>\n for each word
         */
        public String toString(Node curr, String prev) {
            StringBuilder fullString = new StringBuilder();

            Node[] nodes = curr.getNodes();
            for (int i = 0; i < NUM_LETTERS; i++) {
                if (nodes[i] != null) {
                    if (nodes[i].getValue() > 0) { fullString.append(prev + (char)(i + 'a') + '\n'); }
                    fullString.append(toString(nodes[i], prev + (char)(i + 'a')));
                }
            }

            return fullString.toString();
        }

        /** equals
         *
         * compares a node with another object
         * a node is equal to another node iff their values match and all children are equal to their counterparts
         * (this.nodes[i].equals(o.nodes[i]))
         *
         * @param o: object to compare the node with
         * @return true if equal, false if not equal
         */
        @Override
        public boolean equals(Object o) {
            if (o == null) { return false; }
            else if (this == o) { return true; }
            else if (this.getClass() != o.getClass()) { return false; }

            Node oNode = (Node)o;

            if (this.value != oNode.value) { return false; }

            boolean check = true;
            for (int i = 0; i < NUM_LETTERS && check; i++) {
                if ((this.getNodes()[i] == null && oNode.getNodes()[i] != null)
                || (this.getNodes()[i] != null && oNode.getNodes()[i] == null)) { check = false; }
                if (this.getNodes()[i] != null && oNode.getNodes()[i] != null) {
                    check = this.getNodes()[i].equals(oNode.getNodes()[i]);
                }
            }
            return check;
        }

        public int getValue() { return value; }
        public void addValue() { value++; }

        public Node[] getNodes() { return nodes; }
    }
}