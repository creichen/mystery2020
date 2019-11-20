package mystery2020;

import mystery2020.datastructures.UnionFind;

public class MTypeComparison {
	private UnionFind<MType> eqset = new UnionFind<>();
	private Configuration config;
	
	public MTypeComparison(Configuration conf) {
		this.config = conf;
	}
	
	public boolean
	isEq(MType lhs, MType rhs) {
		//System.err.println("comparing " + lhs + "@" + System.identityHashCode(lhs) + " and " + rhs + "@" + System.identityHashCode(rhs) + " on " + this.eqset);
		if (this.eqset.areInSameSet(lhs, rhs)) {
			//System.err.println(" -> eqset match");
			return true;
		}
		//System.err.println(" -> no match, eqsetting and continuing");
		this.eqset.merge(lhs, rhs);
		lhs = this.config.type_names_TYPE.get().normaliseType(lhs);
		rhs = this.config.type_names_TYPE.get().normaliseType(rhs);
		this.eqset.merge(lhs, rhs);
		//System.err.println(" --> postnormalise: " + lhs + "@" + System.identityHashCode(lhs) + " and " + rhs + "@" + System.identityHashCode(rhs));
		boolean result = lhs.isStructurallyEqual(rhs, this);
		//System.err.println(" => " + result);
		return result;
	}
}
