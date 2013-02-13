package j.collections;

import java.util.TreeSet;
import java.util.Iterator;

/**
 * Represents a set of elements implemented using an AVL tree.
 * This class is not thread-safe.
 * @author Lucas Tan
 */
public class AVLTreeSet<E> extends TreeSet
{
    private static class Node
    {
        public Node left;
        public Node right;
        
        /** Size of subtree including itself. */
        public int size;
        
        /** Height of subtree including itself. 
         * Height of leaf == 1 
         * Height of leaf's left/right child == 0 since no left/right child
         */
        public int height;
        
        public E value;
    }

    /** Represents an empty node. Mainly for convenience. */
    private static final Node emptyNode = new Node();
    static
    {
        emptyNode.height = emptyNode.size = 0;
    }

    private final Comparator<E> comp;

    public AVLTreeSet()
    {
        // TODO check that E implements comparable
    }

    public AVLTreeSet(Comparator<E> comp)
    {
        // TODO check comp is not null
    }

    /**
     * Rotates x to the left and returns x's right child.
     * Assumes x and x's right child are not empty.
     */
    private static Node singleRotateLeft(Node x)
    {
        /*
         *  x          y
         *   \        / \
         *    y   => x   z
         *   / \      \
         *  w   z      w
         */

        final Node y = x.right;
        final Node z = y.right;
        final Node w = y.left; // can be empty

        x.right = w;
        y.left = x;

        x.height = 1 + Math.max(w.height, x.left.height);
        y.height = 1 + Math.max(x.height, z.height);

        y.size += 1 + x.left.size;
        x.size -= 1 + z.size; // x.size == x.left.size + w.size + 1;

        return y;
    }

    /**
     * Rotates x to the right and returns x's left child.
     * Assumes x and x's left child are not empty.
     */
    private static Node singleRotateRight(Node x)
    {
        /*
         *     x       y
         *    /       / \
         *   y    => z   x
         *  / \         /
         * z   w       w
         */

        final Node y = x.left;
        final Node z = y.left;
        final Node w = y.right; // can be empty

        x.left = w;
        y.right = x;

        x.height = 1 + Math.max(w.height, x.right.height);
        y.height = 1 + Math.max(z.height, x.height);

        y.size += 1 + x.right.size;
        x.size -= 1 + z.size;

        return y;
    }

    /**
     * Assumes s, s's left and s's left's right are not empty.
     */
    private static Node doubleRotateRight(Node s)
    {
        /*
        *      s          s       y
        *     /          /       / \
        *     x     =>  y    => x   s
        *      \       / \         /
        *       y     x   z       z
        *        \
        *         z
        */

        s.left = singleRotateLeft(s.left);
        return singleRotateRight(s);
    }

    /**
     * Assumes s, s's right and s's right's left are not empty.
     */
    private static Node doubleRotateLeft(Node s)
    {
        /*
        *    s      s            y
        *     \      \          / \
        *      x  =>  y    =>  s   x
        *     /      / \        \   
        *    y      z   x        z
        *   /    
        *  z       
        */

        s.right = singleRotateRight(s.right);
        return singleRotateLeft(s);
    }

    
}

