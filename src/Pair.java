/**
 * Created by zhenghuiqiang on 17/10/20.
 */
public class Pair<A, B> {

	public A first;
	public B second;

	public Pair(A value0, B value1) {
		this.first = value0;
		this.second = value1;
	}

	public B getSecond() {
		return second;
	}

	public void setSecond(B second) {
		this.second = second;
	}

	public A getFirst() {
		return first;
	}

	public void setFirst(A first) {
		this.first = first;
	}
}
