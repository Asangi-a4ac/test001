package tcp;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;

public class Unit {
	HashMap<String, Integer> component;
	static Character separator = '#';
	static HashMap<String, Unit> SIDict = new HashMap<String, Unit>(){
		{
			put("rad",	Unit.eval("1"));
			put("sr",	Unit.eval("1"));
			put("Hz",	Unit.eval("1 / s"));
			put("N",	Unit.eval("kg m / s^2"));
			put("Pa",	Unit.eval("N / m^2"));
			put("J",	Unit.eval("N m"));
			put("W",	Unit.eval("J / s"));
			put("C",	Unit.eval("A s"));
			put("V",	Unit.eval("W / A"));
			put("F",	Unit.eval("C / V"));
			put("É∂",	Unit.eval("V / A"));
			put("S",	Unit.eval("A / V"));
			put("Wb",	Unit.eval("V s"));
			put("T",	Unit.eval("Wb / m^2"));
			put("H",	Unit.eval("Wb / A"));
			put("Åé", 	Unit.eval("K"));
			put("lm",	Unit.eval("cd sr"));
			put("lx",	Unit.eval("lm / m^2"));
			put("Bq",	Unit.eval("1 / s"));
			put("Gy",	Unit.eval("J / kg"));
			put("Sv",	Unit.eval("J / kg"));
			put("kat",	Unit.eval("mol / s"));
		}
	};
	
	Unit(HashMap<String, Integer> comp){
		component = comp;
	}
	
	Unit(){
		HashMap<String, Integer> empty = new HashMap<String, Integer>();
		component = empty;
	}
	
	boolean equals(Unit other) {
		HashMap<String, Integer> dieser = this.normalize().component;
		HashMap<String, Integer> ander = other.normalize().component;
		return dieser.equals(ander);
	}
	
	public Unit normalize() {
		HashMap<String, Integer> normalized = new HashMap<String, Integer>();
		for (Entry<String, Integer> entry : this.component.entrySet()) {
		    String base = entry.getKey().trim();
		    Integer exponent = entry.getValue();
		    
		    if(exponent != 0 && !base.isBlank()) {
		    	normalized.put(base, exponent);
		    }
		}
		
		return new Unit(normalized);
	}
	
	public Unit SIize() {
		for (Entry<String, Integer> entry : this.component.entrySet()) {
		    String unit = entry.getKey();
		    if(SIDict.keySet().contains(unit)) {
		    	Unit converted = SIDict.get(unit);
		    	Integer exponent = this.component.get(unit);
		    	Unit toConvert = Unit.eval(unit + "^" + exponent);
		    	return this.div(toConvert).mult(converted.pow(exponent)).SIize();
		    }
		}
		return this;
	}
	
	public String toString() {
		String str = "";
		for (Entry<String, Integer> entry : this.component.entrySet()) {
		    String base = entry.getKey();
		    Integer exponent = entry.getValue();
		    
		    if(exponent != 0) {
		    	str += base;
			    if(exponent != 1) {
			    	str += "^" + exponent.toString();
			    }
			    str += " ";
		    }
		}
		if ("".equals(str)) {
			return "1";
		} else {
			return str.trim();
		}
	}
	
	public Unit mult(Unit other) {
		HashMap<String, Integer> product = new HashMap<String, Integer>();
		
		for (Entry<String, Integer> entry : this.component.entrySet()) {
		    product.put(entry.getKey(), entry.getValue());
		}
		
		for (Entry<String, Integer> entry : other.component.entrySet()) {
		    String key = entry.getKey();
		    Integer value = entry.getValue();
			if(product.containsKey(key)) {
				product.put(key, product.get(key) + value);
			} else {
				product.put(key, value);
			}
		}
		
		return new Unit(product).normalize();
	}
	
	public Unit div(Unit other) {
		HashMap<String, Integer> product = new HashMap<String, Integer>();
		
		for (Entry<String, Integer> entry : this.component.entrySet()) {
		    product.put(entry.getKey(), entry.getValue());
		}
		
		for (Entry<String, Integer> entry : other.component.entrySet()) {
		    String key = entry.getKey();
		    Integer value = entry.getValue();
			if(product.containsKey(key)) {
				product.put(key, product.get(key) - value);
			} else {
				product.put(key, -value);
			}
		}
		
		return new Unit(product).normalize();
	}
	
	public Unit add(Unit other) throws NonEqualUnitsException{
		if(this.equals(other)) {
			return this;
		} else {
			throw new NonEqualUnitsException("+" + separator + this.toString() + separator + other.toString());
		}
	}

	public Unit sub(Unit other) throws NonEqualUnitsException{
		if(this.equals(other)) {
			return this;
		} else {
			throw new NonEqualUnitsException("-" + separator + this.toString() + separator + other.toString());
		}
	}

	public Unit pow(int other) {
		HashMap<String, Integer> result = new HashMap<String, Integer>();
		for (Entry<String, Integer> entry : this.component.entrySet()) {
		    result.put(entry.getKey(), entry.getValue() * other);
		}
		return new Unit(result).normalize();
	}
	
	public static Unit eval(String str) {
		HashMap<String, Integer> evaled = new HashMap<String, Integer>();
		
		// maybe empty string comes, maybe not, i don't know!
		if("".equals(str) || "1".equals(str)) {
			return new Unit();
		}
		
		// if it does not have parentheses
		if(str.indexOf("(") == -1) {
			// if it does not have divisions
			if(str.indexOf("/") == -1) {
				String[] units = str.split(" ");
				
				for(String cood : units) {
					String[] words = cood.split("\\^");
					String unit = words[0];
					Integer exponent = 1;
					if(words.length > 1) {
						exponent = Integer.parseInt(words[1]);
					}
					if(!unit.isBlank()) {
						evaled.put(unit, exponent);
					}
				}
				
				return new Unit(evaled).normalize();
			} else {
				String[] terms = str.split("/");
				Unit u = Unit.eval(terms[0]);
				
				for(int i = 1; i < terms.length; i++) {
					u = u.div(Unit.eval(terms[i]));
				}
				
				return u.normalize();
			}
		} else {
			int leftParenIndex = 0;
			int rightParenIndex = 0;
			for(int i = 0; i < str.length(); i++) {
				if(str.charAt(i) == '(') {
					leftParenIndex = i;
				}
				if(str.charAt(i) == ')') {
					rightParenIndex = i;
					// evaluates this parentheses
					String inParen = str.substring(leftParenIndex + 1, rightParenIndex);
					return Unit.eval(str.substring(0, leftParenIndex) + Unit.eval(inParen) + str.substring(rightParenIndex + 1)).normalize();
				}
			}
		}
		return null;
	}
	
	public Unit sqrt() throws FractionalUnitException {
		HashMap<String, Integer> root = new HashMap<String, Integer>();
				
		for (Entry<String, Integer> entry : this.component.entrySet()) {
		    String key = entry.getKey();
		    Integer value = entry.getValue();
			if(value % 2 == 0) {
				root.put(key, value / 2);
			} else {
				throw new FractionalUnitException(this.toString());
			}
		}
		
		return new Unit(root).normalize();
	}
	
	public Unit add(int other) throws NonEqualUnitsException{
		return this.add(new Unit());
	}
	
	public Unit sub(int other) throws NonEqualUnitsException{
		return this.sub(new Unit());
	}
}
