package tcp;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Objects;
import java.util.Arrays;

// This program is called TCP, except it's not that TCP you're thinking of.
public class TexCertificationProgram {

	static HashMap<String, Integer> yen_db;
	static String separator = "`";
	static ArrayList<String> functions;
	static boolean DEBUGMODE = false;
	
	public static void main(String[] args) throws IOException {
		HashMap<String, Integer> u = new HashMap<String, Integer>();
		u.put("kg", 2);
		u.put("m", 1);
		u.put("s", 0);
		u.put("A", -1);
		u.put("K", -2);
		Unit un = new Unit(u);
		// System.out.println(un.toString());
		
		HashMap<String, Integer> v = new HashMap<String, Integer>();
		v.put("J", 1);
		v.put("m", 1);
		v.put("s", -1);
		v.put("mol", 0);
		Unit vn = new Unit(v);
		if(DEBUGMODE) { System.out.println(vn.SIize().toString()); }
		// System.out.println(Unit.eval("kg^2 m^1 s^0 A^-1").toString());
		
		yen_db = new HashMap<String, Integer>();
		yen_db.put("\\frac", 2);
		yen_db.put("\\sqrt", 1);
		
		functions = new ArrayList<String>();
		functions.add("\\frac");
		functions.add("\\sin");
		functions.add("\\cos");
		functions.add("\\tan");
		functions.add("\\exp");
		functions.add("\\log");
		functions.add("\\sqrt");
		
		InputStreamReader isr = new InputStreamReader(System.in);
		BufferedReader br = new BufferedReader(isr);
		
		// String stream = "\\int a_1 dx+b_{2+3}*3dc_{c^{2}_{\\omega}}(\\frac{\\frac{1}{2}}{\\frac{3}{4}}5 + \\log{2}) = \\frac{1}{23} \\alpha (r+1)(r+2) \\frac{dy}{dx}";
		// String stream = "C = \\varepsilon \\frac{dV}{S}";
		String stream = "";
		
		System.out.println("数式(TeX)を入力してください");
		
		// example testcase (worked): \frac{1+2}{3+4}+5\sqrt{\frac{6}{7+8}}
		// example testcase (worked): a*b-x^2=\sqrt{\frac{z}{2}}
		
		try {
			stream = new String(br.readLine());
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		if(DEBUGMODE) { System.out.println("stream: " + stream); }
		ArrayList<String> dns = DivideNameString(stream);
		if(DEBUGMODE) { System.out.println("dns: " + dns); }
		
		ArrayList<String> ip = InfixProgram(dns);
		// Why is ipv6 a string? Because it's fun!
		if(DEBUGMODE) { System.out.println("ip: " + ip); }
		
		ArrayList<String> arp = ArgumentRecognitionProgram(ip);
		if(DEBUGMODE) { System.out.println("arp: " + arp); }
		ArrayList<String> ppp = PutintoPostfixProgram(ip);
		if(DEBUGMODE) { System.out.println("ppp: " + ppp); }
		HashMap<String, Unit> udp = UnitDigitizeProgram(arp);
		if(DEBUGMODE) { System.out.println("udp: " + udp); }
		boolean ftp = FinalTestifyProgram(ppp, udp);
		if(DEBUGMODE) { System.out.println("ftp: " + ftp); }
	}
	
	// The first program is called DNS.
	// It splits the string into "words."
	private static ArrayList<String> DivideNameString(String stream) {
		// HashMap<String, Integer> yen_db = new HashMap<String, Integer>();
		
		// yen_db.put("\\frac", 2);
		// yen_db.put("\\log", 1);
		
		ArrayList<String> dns = new ArrayList<String>();
		dns.add("");
		int lastidx = 0; // the last available index of dns
		// check the letters one by one
		for(int i = 0; i < stream.length(); i++) {
			String c = Character.toString(stream.charAt(i));
			if(DEBUGMODE) { System.out.println("resh: " + dns + " sin: " + c); }
			// if you see an underscore, it's a part of the previous "word"
			if ("_".equals(c)) {
				dns.set(lastidx, dns.get(lastidx) + c);
				i++;
				int nesting = 0;
				do {
					c = Character.toString(stream.charAt(i));
					dns.set(lastidx, dns.get(lastidx) + c);
					if ("{".equals(c)) {
						nesting++;
					} else if("}".equals(c)) {
						nesting--;
					}
					// System.out.println("`"+c+"`"+nesting);
					i++;
				}while(nesting > 0);
				i--;
			} else {
				if("\\".equals(c)) {
					dns.add(c);
					lastidx++;
					i++;
					c = Character.toString(stream.charAt(i));
					
					// the word
					while(c.matches("[a-zA-Z]") && i < stream.length()) {
						dns.set(lastidx, dns.get(lastidx) + c);
						// System.out.println("`"+c+"`"+nesting);
						i++;
						if(i != stream.length()) {
							c = Character.toString(stream.charAt(i));
						}
					}
					i--;

					/*
					// arguments
					String word = dns.get(lastidx);
					int num_args = yen_db.containsKey(word) ? yen_db.get(word) : 0;
					System.out.println("beth: " + word + " gimel: " + num_args);
					
					for(int arg = 0; arg < num_args; arg++) {
						c = Character.toString(stream.charAt(i));
						
					}
					*/
					// not done
				} else {
					if (c.matches("[0-9]")) {
						String word;
						if(dns.size() > 0 && !"".equals(dns.get(lastidx))) {
							word = dns.get(lastidx);
						} else {
							word = " ";
						}
						Character last = word.charAt(word.length()-1);
						if(Character.toString(last).matches("[0-9]")) {
							dns.set(lastidx, dns.get(lastidx) + c);
						} else {
							dns.add(c);
							lastidx++;
						}
					} else {
						// System.out.println("idk for " + c);
						dns.add(c);
						lastidx++;
					}
				}
				// dns.set(lastidx, dns.get(lastidx) + c);
			}
		}
		
		for(int i = lastidx; i >= 0; i--) {
			if(i < dns.size() && dns.get(i).equals(" ")) {
				dns.remove(i);
			}
		}
		dns.removeIf(word -> "".equals(word));
		return dns;
	}
	
	// The second program is called IP.
	// It changes \f{a}{b} into \f{a`b}.
	// All pun intended.
	private static ArrayList<String> InfixProgram(ArrayList<String> dns){
		if(DEBUGMODE) { System.out.println("he: " + concatenate(dns)); }
		ArrayList<String> ip = new ArrayList<String>();
				
		for(int i=0; i<dns.size(); i++) {
			if(dns.get(i).matches("\\\\.*")) {
				if(DEBUGMODE) { System.out.println("dalet"); }
				ip.add(dns.get(i));
				String word = dns.get(i);
				int args = 0;
				if(yen_db.containsKey(word)) {
					args = yen_db.get(word);
				}
				int parentheses = 0;
				ArrayList<String> dns2 = new ArrayList<String>(); // for recursion
				while(args >= 1) {
					i++;
					if(i >= dns.size()) {
						break;
					}
					word = dns.get(i);
					if("}".equals(word)) {
						parentheses--;
						if(parentheses == 0) {
							args--;
							i++;
							ArrayList<String> ipsec = InfixProgram(dns2);
							for(String ipv4 : ipsec) {
								ip.add(ipv4);
							}
							if(args >= 1) {
								ip.add(separator);
							} else {
								ip.add("}");
							}
							i--;
						} else {
							dns2.add(word);
						}
					} else {
						if("{".equals(word)) {
							if(parentheses == 0) {
								if(!separator.equals(ip.get(ip.size()-1))) {
									ip.add(word);
								}
								dns2 = new ArrayList<String>();
							} else {
								dns2.add(word);
							}
							parentheses++;
						} else {
							dns2.add(word);
						}
					}
					if(DEBUGMODE) { System.out.println("gimel: " + word + " chet: " + args); }
				}
			} else {
				ip.add(dns.get(i));
			}
		}
		if(DEBUGMODE) { System.out.println("vav: " + concatenate(ip)); }
		return ip;
	}
	
	// The third program is called ARP.
	// It lists up all the variables used.
	private static ArrayList<String> ArgumentRecognitionProgram(ArrayList<String> ip){
		ArrayList<String> arp = new ArrayList<String>();
		
		// System.out.println("kaf: " + ip);
		
		for(int i=0; i<ip.size(); i++) {
			String word = ip.get(i);
			if(DEBUGMODE) { System.out.println("sin:" + word); }
			if("\\int".equals(word)) {
				int parentheses = 0;
				ArrayList<String> ipsec = new ArrayList<String>();
				while(true) {
					i++;
					word = ip.get(i);
					if(word.matches("[({]")) {
						parentheses++;
					} else if(word.matches("[)}]")) {
						parentheses--;
					}
					if(word.matches("[+-=]") && parentheses == 0 || i == ip.size()-1) {
						break;
					} else {
						if("d".equals(word)) {
							if(i == ip.size()-1 || ip.get(i+1).matches("[+-]")) {
								arp.add(word);
							}
						} else {
							ipsec.add(word);
						}
					}
				}
				ArrayList<String> rarp = ArgumentRecognitionProgram(ipsec);
				for(String wert: rarp) {
					arp.add(wert);
				}
			} else if("\\frac".equals(word)){
				i++;
				if(i < ip.size()) {
					word = ip.get(i);
				}
				ArrayList<String> numerator = new ArrayList<String>();
				ArrayList<String> denominator = new ArrayList<String>();
				for(int j=0; j<2; j++) {
					int parentheses = 0;
					do {
						boolean addWord = true;
						if("{".equals(word)) {
							if(parentheses == 0) {
								addWord = false;
							}
							parentheses++;
						} else if("}".equals(word)) {
							parentheses--;
							if(parentheses == 0) {
								addWord = false;
							}
						} else if("`".equals(word)) {
							if(parentheses == 1) {
								addWord = false;
								j++;
							}
						}
						if(addWord){
							switch(j) {
							case 0:
								numerator.add(word);
								break;
							case 1:
								denominator.add(word);
								break;
							}
						}
						if(parentheses > 0) {
							i++;
							if(i < ip.size()) {
								word = ip.get(i);
							}
						}
					}while(parentheses > 0);
				}
				// System.out.println("tet: " + numerator);
				// System.out.println("yud: " + denominator);
				ArrayList<String> localARPn = ArgumentRecognitionProgram(numerator);
				ArrayList<String> localARPd = ArgumentRecognitionProgram(denominator);
				
				// System.out.println("mem: " + localARPn);
				// System.out.println("nun: " + localARPd);
				
				
				int startj = 0;
				if("d".equals(numerator.get(0)) && "d".equals(denominator.get(0))) {
					startj = 1;
				}
				for(int j = startj; j < localARPn.size(); j++) {
					arp.add(localARPn.get(j));
				}
				for(int j = startj; j < localARPd.size(); j++) {
					arp.add(localARPd.get(j));
				}
				
			} else if(word.matches("[a-zA-Z].*")){
				arp.add(word);
			} else if(word.matches("\\\\.*")) {
				if(functions.indexOf(word) < 0) {
					arp.add(word);
				}
			}
		}
		
		ArrayList<String> uniqarp = new ArrayList<String>();
		for(String word : arp) {
			if(!uniqarp.contains(word)) {
				uniqarp.add(word);
			}
		}
		return uniqarp;
	}
	
	// The third (actually fourth) program is called PPP.
	// It turns IP into the Postfix notation.
	// Wait, I need to write the Node class first.
	// Not actually.
	// reference: https://qiita.com/thtitech/items/91e2456c989ca969850d
	private static ArrayList<String> PutintoPostfixProgram(ArrayList<String> ip) {
		ArrayList<String> stack = new ArrayList<String>();
		ArrayList<String> ppp = new ArrayList<String>();
		
		// opTable[a][b] <=> a has the lower or equal priority than b
		// not correct way to describe but you get the idea
		HashMap<String, HashMap<String, Boolean>> opTable = new HashMap<String, HashMap<String, Boolean>>();
		
		HashMap<String, Boolean> addTable = new HashMap<String, Boolean>();
		addTable.put("+", true);
		addTable.put("-", true);
		addTable.put("*", true);
		addTable.put("^", true);
		addTable.put("=", false);
		opTable.put("+", addTable);
		
		HashMap<String, Boolean> subTable = new HashMap<String, Boolean>();
		subTable.put("+", true);
		subTable.put("-", true);
		subTable.put("*", true);
		subTable.put("^", true);
		subTable.put("=", false);
		opTable.put("-", subTable);
		
		HashMap<String, Boolean> mulTable = new HashMap<String, Boolean>();
		mulTable.put("+", false);
		mulTable.put("-", false);
		mulTable.put("*", false);
		mulTable.put("^", true);
		mulTable.put("=", false);
		opTable.put("*", mulTable);
		
		HashMap<String, Boolean> powTable = new HashMap<String, Boolean>();
		powTable.put("+", false);
		powTable.put("-", false);
		powTable.put("*", false);
		powTable.put("^", false);
		powTable.put("=", false);
		opTable.put("^", powTable);
		
		HashMap<String, Boolean> eqlTable = new HashMap<String, Boolean>();
		eqlTable.put("+", true);
		eqlTable.put("-", true);
		eqlTable.put("*", true);
		eqlTable.put("^", true);
		eqlTable.put("=", false);
		opTable.put("=", eqlTable);
		
		
		ArrayList<Boolean> isPrevVariable = new ArrayList<Boolean>(Arrays.asList(false));
		
		for(String word : ip) {
			if(DEBUGMODE) { System.out.println("kaf: " + stack + " word: " + word); }
			// if it's a constant
			if(word.matches("[0-9a-zA-Z].*") || (word.matches("\\\\.*") && functions.indexOf(word) < 0)) {
				if(DEBUGMODE) { System.out.println("lamed" + isPrevVariable); }
				ppp.add(word);
				if(isPrevVariable.get(isPrevVariable.size()-1)) {
					ppp.add("*");
				}
				isPrevVariable.set(isPrevVariable.size()-1, true);
			// if it's an open parentheses
			} else if(word.matches("[({]")) {
				if(DEBUGMODE) { System.out.println("mem" + isPrevVariable); }
				stack.add(word);
				isPrevVariable.add(false);
			// if it's a close parentheses, or a grave
			} else if(word.matches("[})]") || separator.equals(word)) {
				// pop and add to the queue until you find the left parenthesis
				if(DEBUGMODE) { System.out.println("nun" + isPrevVariable); }
				String stackTop = stack.get(stack.size()-1);
				stack.remove(stack.size()-1);
				while(!stackTop.matches("[({]")) {
					ppp.add(stackTop);
					stackTop = stack.get(stack.size()-1);
					stack.remove(stack.size()-1);
				}
				if(separator.equals(word)) {
					stack.add("{");
				} else {
					if(stack.size() > 0) {
						stackTop = stack.get(stack.size()-1);
						if(stackTop.matches("\\\\.*")) {
							stack.remove(stack.size()-1);
							ppp.add(stackTop);
						}
					}
				}
				isPrevVariable.remove(isPrevVariable.size()-1);
				boolean ipv = isPrevVariable.get(isPrevVariable.size()-1);
				if(ipv && (")".equals(word) || "}".equals(word))) {
					ppp.add("*");
				}
				if("`".equals(word)) {
					isPrevVariable.add(false);
				}
				isPrevVariable.set(isPrevVariable.size()-1, ")".equals(word) || "}".equals(word));
			// if it's an operator (including functions)
			} else {
				if(DEBUGMODE) { System.out.println("samekh: " + stack + isPrevVariable); }
				if(stack.size() > 0) {
					String stackTop = stack.get(stack.size()-1);
					// if lower priority
					// function has the lowest priority
					if(word.matches("\\\\.*") && !"\\int".equals(word)) {
						if(isPrevVariable.get(isPrevVariable.size()-1)) {
							stack.add("*");
						}
						//while(stack.size() > 0 && stackTop.matches("(\\\\.*|[+\\-*])")) {
						while(stack.size() > 0 && stackTop.matches("(\\\\.*)")) {
							stack.remove(stack.size()-1);
							ppp.add(stackTop);
							if(stack.size()>0) {
								stackTop = stack.get(stack.size()-1);
							}
						}
					}
					while(stack.size() > 0 && opTable.containsKey(word) && opTable.get(word).containsKey(stackTop) && opTable.get(word).get(stackTop)) {
						stackTop = stack.get(stack.size()-1);
						stack.remove(stack.size()-1);
						ppp.add(stackTop);
						if(stack.size()>0) {
							stackTop = stack.get(stack.size()-1);
						}
					}
					stack.add(word);
					
					// push to the stack
				} else {
					stack.add(word);
					if(DEBUGMODE) { System.out.println("ayin! " + stack); }
				}
				isPrevVariable.set(isPrevVariable.size()-1, false);
			}
		}
		while(stack.size() > 0) {
			ppp.add(stack.remove(stack.size()-1));
		}
		return ppp;
	}
	
	// The fifth program is UDP.
	// It takes input for units on each variable.
	private static HashMap<String, Unit> UnitDigitizeProgram(ArrayList<String> arp) {
		HashMap<String, Unit> udp = new HashMap<String, Unit>();
		InputStreamReader isr = new InputStreamReader(System.in);
		BufferedReader br = new BufferedReader(isr);
		
		for(String variable: arp) {
			System.out.println(variable + " の単位を入力してください");
			
			String str = null;
			try {
				str = new String(br.readLine());
			} catch (IOException e) {
				e.printStackTrace();
			}
			if(DEBUGMODE) { System.out.println("pe: " + str); }
			udp.put(variable, Unit.eval(str).SIize());
		}
		
		try {
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return udp;
	}
	
	// The last program is FTP.
	// It actually calculates the units.
	private static boolean FinalTestifyProgram(ArrayList<String> ppp, HashMap<String, Unit> udp) {
		ArrayList<Object> stack = new ArrayList<Object>();
		for(String word : ppp) {
			if(DEBUGMODE) { System.out.println("tsadi: " + stack + " qof: " + word); }
			// if it's a variable
			if(word.matches("[a-zA-Z].*") || (word.matches("\\\\.*") && functions.indexOf(word) < 0)) {
				Unit u = udp.get(word);
				if(Objects.equals((Object) u, (Object) null)) {
					u = new Unit();
				}
				stack.add(u);
			// if it's a number
			} else if(word.matches("[0-9]*")){
				stack.add(Integer.parseInt(word));
			} else {
				// if it's an operator
				if(word.matches("[+*-=^]")) {
					Object obj1 = stack.remove(stack.size()-1);
					Object obj2 = stack.remove(stack.size()-1);
					Unit unit1 = andDotTo_u(obj1);
					Unit unit2 = andDotTo_u(obj2);
					
					Unit unit = new Unit();
					switch(word) {
					case "+":
						try {
							unit = unit2.add(unit1);
						} catch (NonEqualUnitsException e) {
							String[] errorInfo = e.getMessage().split(Unit.separator.toString());
							System.out.println("+の左右の次元が異なります");
							System.out.println("左側: " + errorInfo[1]);
							System.out.println("右側: " + errorInfo[2]);
							return false;
						}
						stack.add(unit);
						break;
					case "-":
						try {
							unit = unit2.sub(unit1);
						} catch (NonEqualUnitsException e) {
							String[] errorInfo = e.getMessage().split(Unit.separator.toString());
							System.out.println("-の左右の次元が異なります");
							System.out.println("左側: " + errorInfo[1]);
							System.out.println("右側: " + errorInfo[2]);
							return false;
						}
						stack.add(unit);
						break;
					case "*":
						unit = unit2.mult(unit1);
						stack.add(unit);
						break;
					case "^":
						if(!(obj1 instanceof Unit)) {
							int exp = Integer.parseInt(obj1.toString());
							unit = unit2.pow(exp);
						} else {
							System.out.println("今のところ定数乗にしか対応していません");
							System.out.println("([" + unit2 + "]の[" + obj1 + "]乗を試みました)");
							return false;
						}
						stack.add(unit);
						break;
					case "=":
						if(unit1.equals(unit2)) {
							System.out.println("両辺の単位は一致しています");
							System.out.println("単位: " + unit1);
							return true;
						} else {
							System.out.println("両辺の単位は一致していません");
							System.out.println("左辺: " + unit2);
							System.out.println("右辺: " + unit1);
							return false;
						}
					default:
						System.out.println("論理的に到達しえないプログラムのコードに達しました。　エラーコード: 1番");
					}
				// if it's a function
				} else if(yen_db.containsKey(word)) {
					int numArgs = yen_db.get(word);
					Unit[] units = new Unit[numArgs];
					for(int i = 0; i < numArgs; i++) {
						units[i] = andDotTo_u(stack.remove(stack.size()-1));
					}
					Unit unit = new Unit();
					switch(word) {
					case "\\frac":
						unit = units[1].div(units[0]);
						stack.add(unit);
						break;
					case "\\sqrt":
						try {
							unit = units[0].sqrt();
						} catch (FractionalUnitException e) {
							System.out.println("非整数の冪にはまだ対応していません");
							System.out.println("([" + units[0] + "]の平方根を試みました)");
							return false;
						}
						stack.add(unit);
						break;
					}
				}
			}
		}
		System.out.println("完全な等式が入力されていない可能性があります");
		if(DEBUGMODE) { System.out.println("resh: " + stack); }
		return (Boolean) false;
	}
	
	// For debugging.
	private static String concatenate(ArrayList<String> strings) {
		String strs = "";
		for(String str : strings) {
			strs += str;
		}
		return strs;
	}
	
	// Safe casting to Unit.
	// The name comes from Ruby "&."
	private static Unit andDotTo_u(Object obj) {
		if(obj instanceof Unit) {
			return (Unit) obj;
		} else {
			return new Unit();
		}
	}
}
