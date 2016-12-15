package net.aonsolutions.dump;

public class Threes<A, B, C> {
	private A first;
	private B second;
	private C third;

	public Threes(A first, B second, C third) {
		super();
		this.first = first;
		this.second = second;
		this.third = third;
	}

	public int hashCode() {
		int hashFirst = first != null ? first.hashCode() : 0;
		int hashSecond = second != null ? second.hashCode() : 0;
		int hashThird = third != null ? third.hashCode() : 0;

		return (hashFirst + hashSecond + hashThird) * hashThird + hashSecond + hashFirst;
	}

	public boolean equals(Object other) {
    	if (other instanceof Threes) {
    		Threes<?, ?, ?> otherPair = (Threes<?, ?, ?>) other;
    		return 
    		((  this.first == otherPair.first ||
    			( this.first != null && otherPair.first != null &&
    			  this.first.equals(otherPair.first))) &&
    		 (	this.second == otherPair.second ||
    			( this.second != null && otherPair.second != null &&
    			  this.second.equals(otherPair.second))) &&
    		 (	this.third == otherPair.third ||
	 			( this.third != null && otherPair.third != null &&
	 			  this.third.equals(otherPair.third))) );
    	}

    	return false;
    }

	public String toString() {
		return "(" + first + ", " + second + ", "+ third +")";
	}

	public A getFirst() {
		return first;
	}

	public void setFirst(A first) {
		this.first = first;
	}

	public B getSecond() {
		return second;
	}

	public void setSecond(B second) {
		this.second = second;
	}
	
	public C getThird() {
		return third;
	}

	public void setThird(C third) {
		this.third = third;
	}
}
