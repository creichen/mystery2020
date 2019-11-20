package mystery2020.datastructures;

import java.util.IdentityHashMap;

public class UnionFind<T> {
	private IdentityHashMap<T, Node> identmap = new IdentityHashMap<>(); 
	
	public UnionFind() {
	}
	
	private Node
	findNode(T t) {
		if (!this.identmap.containsKey(t)) {
			Node n = new Node();
			this.identmap.put(t, n);
			return n;
		}
		return this.identmap.get(t);
	}
	
	public void
	merge(T t1, T t2) {
		this.findNode(t1).union(this.findNode(t2));
	}
	
	public boolean
	areInSameSet(T t1, T t2) {
		return this.findNode(t1).find() == this.findNode(t2).find();
	}
	
	private class Node {
		private Node parent = this;
		private int rank = 0;
		public Node() {};
		
		public Node
		find() {
			if (this.parent == this) {
				return this;
			}
			this.parent = this.parent.find();
			return this.parent;
		}
		
		public void
		union(Node other) {
			Node thisRoot = this.find();
			Node otherRoot = other.find();
			if (thisRoot.rank < otherRoot.rank) {
				thisRoot.parent = otherRoot;
				otherRoot.rank += 1;
			} else {
				otherRoot.parent = thisRoot;
				thisRoot.rank += 1;
			}
		}
	}
}
