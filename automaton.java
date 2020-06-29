import java.util.*;

public class automaton {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        HashMap<String, State> fa = new HashMap<String, State>();         //状態の集合 Q に当たる
        HashSet<String> alphabets = new HashSet<String>();     //使う文字列の集合　\Sigma に当たる  空文字列はEPS
        int numAlphabet = sc.nextInt();
        for (int itr = 0; itr < numAlphabet; itr++) {
            String s = sc.next();
            alphabets.add(s);
        }
        int numState = sc.nextInt();      //状態数 タマの数
        int numTransition = sc.nextInt();      //遷移数 要するに矢印の数
        for (int itr = 0; itr < numState; itr++) {      //状態の情報を読み込む
            String stateName = sc.next();
            boolean isAccepted = sc.nextBoolean();
            boolean isStart = sc.nextBoolean();
            State addState = new State(stateName, 0, isAccepted, isStart);
            fa.put(stateName, addState);
        }
        for (int itr = 0; itr < numTransition; itr++) {     //遷移の情報を読み込む
            String transitionFrom = sc.next();
            String transitionChar = sc.next();
            String transitionTo = sc.next();
            if (transitionChar.equals("EPS")) {     //空文字列
                if (fa.get(transitionFrom).equals(fa.get(transitionTo))) {
                    //EPSの遷移で遷移前と遷移後で同じ状態を指していると無限ループになる
                } else {
                    (fa.get(transitionFrom)).addTransition(transitionChar, transitionTo); //遷移を追加
                }
            } else {
                (fa.get(transitionFrom)).addTransition(transitionChar, transitionTo); //遷移を追加
            }
        }
        String computeString = sc.next();   //処理する文字列を受け取る
        testOutput(fa);     //オートマトンの格納状態を確認します
        String[] computeStream = readlang(computeString);
        lp(Compute(fa, alphabets, computeStream, computeString));
    }

    private static class State {    // \delta , q , F  を定義
        String name = "";     //状態遷移図のときの○の中身
        int depth = 0;        //オートマトンの遷移の深さ　図を書くときに要るかも
        boolean isAccepted = false;   //◎かどうか
        boolean isStart = false;      //始点かどうか
        HashMap<String, ArrayList<String>> transition;  //遷移先   入力文字と遷移先

        State(String name, int depth, boolean isAccepted, boolean isStart) {
            this.name = name;
            this.depth = depth;
            this.isAccepted = isAccepted;
            this.isStart = isStart;
            this.transition = new HashMap<String, ArrayList<String>>();
        }

        void addTransition(String readchar, String destination) {//HashMap<String,String>だと読み込む文字(key)の行き先を複数指定できず、DFAしか実装できない hashmap<String,ArrayList<string(行き先)>>にした
            if (!this.transition.containsKey(readchar)) {        //読み込む文字が未定義の場合
                if ((this.transition.get(readchar)) == null || (this.transition.get(readchar)).size() == 0) {
                    ArrayList<String> tmp = new ArrayList<String>();
                    tmp.add(destination);
                    this.transition.put(readchar, tmp);
                }
            } else {
                ArrayList<String> tmp = this.transition.get(readchar);
                tmp.add(destination);
                this.transition.put(readchar, tmp);
            }
        }
    }

    static void testOutput(HashMap<String, State> automa) {// for graphiz
        lp("digraph G {");
        lp("\tempty [label = \"\" shape = plaintext];");
        for (String ki : automa.keySet()) {
            if (!automa.get(ki).isAccepted) {
                lp("\t" + ki + ";");
            } else {
                lp("\t" + ki + "[shape = doublecircle];");
            }
        }
        lp("");
        for (String ki : automa.keySet()) {
            State s = automa.get(ki);
            if (s.isStart) {
                lp("\tempty -> " + ki + ";");
            }
            HashMap<String, String> destination = new HashMap<String, String>();
            for (String c : s.transition.keySet()) {
                ArrayList<String> t = s.transition.get(c);
                for (String ss : t) {
                    if (c.equals("EPS")) {
                        if (destination.containsKey((ki + " -> " + ss))) {
                            destination.put(ki + " -> " + ss, destination.get(ki + " -> " + ss) + ",ε");
                        } else {
                            destination.put(ki + " -> " + ss, "ε");
                        }
                    } else {
                        if (destination.containsKey((ki + " -> " + ss))) {
                            destination.put(ki + " -> " + ss, destination.get(ki + " -> " + ss) + "," + c);
                        } else {
                            destination.put(ki + " -> " + ss, c);
                        }
                    }
                }
            }
            for (String ss : destination.keySet()) {
                lp("\t" + ss + " [label =\"" + destination.get(ss) + "\"];");
            }
        }
        lp("}");
    }

    static void easyOutput(HashMap<String, State> automa) {//人間にわかりやすいような出力
        for (String ki : automa.keySet()) {
            lp("state: " + ki);
            State s = automa.get(ki);
            for (String c : s.transition.keySet()) {
                ArrayList<String> t = s.transition.get(c);
                for (String ss : t) {
                    lp("\t trans: " + ki + " -> " + ss + " alphabet: " + c);
                }
            }
        }
    }

    static String[] readlang(String s) {
        boolean isEPS = false;
        ArrayList<String> ar = new ArrayList<String>();
        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) == '[') {
                isEPS = true;
            } else if (s.charAt(i) == ']') {
                ar.add("EPS");
                isEPS = false;
            } else if (!isEPS) {
                ar.add(s.charAt(i) + "");
            }
        }
        String[] res = new String[ar.size()];
        for (int i = 0; i < ar.size(); i++) {
            res[i] = ar.get(i);
        }
        return res;
    }

    static boolean Compute(HashMap<String, State> fa, HashSet<String> alphabets, String[] comp, String computeString) {
        lp("digraph G {");
        lp("\tgraph [");
        lp("\t\tlabel=\"input: " + computeString + "\",");
        lp("\t ];");
        lp("");
        lp("\tempty [label = \"\" shape = plaintext];");
        HashSet<String> transManage = new HashSet<String>();
        HashSet<String> stateManage = new HashSet<String>();
        ArrayList<Path> nexttmpState = new ArrayList<Path>();      //EPSなどの処理も行った上での次の遷移先を保管する。
        int findEPS = 0;
        String startState = "";
        boolean ans = false;
        Stack<Path> computeTask = new Stack<Path>();  //計算タスク
        for (String searchState : fa.keySet()) {
            if (fa.get(searchState).isStart) {
                startState = searchState;
                stateManage.add("\t\"" + searchState + " dep:0\";");
                break;
            }
        }
        nexttmpState.add(new Path(0, -1, 0, "", startState));
        while (nexttmpState.size() > findEPS) {
            Path checkEPS = nexttmpState.get(findEPS);
            if ((fa.get(checkEPS.nowState).transition).containsKey("EPS")) {
                ArrayList<String> pointEPS = (fa.get(checkEPS.nowState).transition).get("EPS");
                for (String transEPS : pointEPS) {
                    nexttmpState.add(new Path(checkEPS.readchar, checkEPS.fromDepth, checkEPS.nowDepth, checkEPS.fromState, transEPS));
                }
            } else {
                //初期状態にEPSがある場合の遷移の列挙でEPSがなければそれ以上遷移が増えない
            }
            findEPS++;
        }
        for (Path cpt : nexttmpState) {
            transManage.add("\t\"empty\" -> \"" + cpt.nowState + " dep:0\";");
            computeTask.add(cpt);
        }
        for (int i = 0; i < comp.length; i++) {
            nexttmpState = new ArrayList<Path>();      //EPSなどの処理も行った上での次の遷移先を保管する。
            Path tmp;
            while (!computeTask.isEmpty()) {
                tmp = computeTask.peek();
                if (tmp.readchar != i) {
                    break;
                } else {
                    tmp = computeTask.pop();
                    if (!alphabets.contains(comp[tmp.readchar])) {//含まれていない場合はエラーを返す。
                        lp("Error! undefined char used!");
                        return false;
                    } else {  //含まれている場合処理をします
                        ArrayList<String> transTo = (fa.get(tmp.nowState).transition).get(comp[tmp.readchar]);
                        if (transTo == null) {    //未定義などで遷移先が定義されてない場合
                            if (fa.get(tmp.nowState).isAccepted) {
                                ans = true;
                            }
                        } else {
                            for (String transitionTo : transTo) {
                                nexttmpState.add(new Path(tmp.readchar + 1, tmp.nowDepth, tmp.nowDepth + 1, tmp.nowState, transitionTo));
                            }
                        }
                    }
                }
            }
            findEPS = 0;
            while (nexttmpState.size() != findEPS) {        //空文字で遷移できるものがある場合を探索する
                Path checkEPS = nexttmpState.get(findEPS);
                if ((fa.get(checkEPS.nowState).transition).containsKey("EPS")) {
                    ArrayList<String> pointEPS = (fa.get(checkEPS.nowState).transition).get("EPS");
                    for (String transEPS : pointEPS) {
                        nexttmpState.add(new Path(checkEPS.readchar, checkEPS.fromDepth, checkEPS.nowDepth, checkEPS.fromState, transEPS));
                    }
                } else {
                    //
                }
                findEPS++;
            }
            for (Path addStack : nexttmpState) {    //遷移をすべて列挙できたので次の探査スタックに入れる
                if (fa.get(addStack.nowState).isAccepted) {
                    stateManage.add("\t\"" + addStack.nowState + " dep:" + addStack.nowDepth + "\" [shape = doublecircle];");
                } else {
                    stateManage.add("\t\"" + addStack.nowState + " dep:" + addStack.nowDepth + "\";");
                }

                if (addStack.fromDepth >= 0) {
                    if (fa.get(addStack.fromState).isAccepted) {
                        stateManage.add("\t\"" + addStack.fromState + " dep:" + addStack.fromDepth + "\" [shape = doublecircle];");
                    } else {
                        stateManage.add("\t\"" + addStack.fromState + " dep:" + addStack.fromDepth + "\";");
                    }
                }
                if (addStack.readchar - 1 < comp.length) {
                    if (comp[addStack.readchar - 1].equals("EPS")) {
                        transManage.add("\t\"" + (addStack.fromState + " dep:" + (addStack.fromDepth)) + "\" -> \"" + addStack.nowState + " dep:" + addStack.nowDepth + "\" [label = \"ε\"];");
                    } else {
                        transManage.add("\t\"" + (addStack.fromState + " dep:" + (addStack.fromDepth)) + "\" -> \"" + addStack.nowState + " dep:" + addStack.nowDepth + "\" [label = \"" + comp[addStack.readchar - 1] + "\"];");
                    }
                } else if (addStack.readchar - 1 >= comp.length) {
                    transManage.add("\t\"" + (addStack.fromState + " dep:" + (addStack.fromDepth)) + "\" -> \"" + addStack.nowState + " dep:" + addStack.nowDepth + ";");
                }
                computeTask.push(addStack);
            }
        }
        while (!computeTask.isEmpty()) {
            Path managePath = computeTask.pop();
            if (fa.get(managePath.nowState).isAccepted) {
                stateManage.add("\t\"" + managePath.nowState + " dep:" + managePath.nowDepth + "\" [shape = doublecircle];");
            } else {
                stateManage.add("\t\"" + managePath.nowState + " dep:" + managePath.nowDepth + "\";");
            }
            if (fa.containsKey(managePath.fromState) && fa.get(managePath.fromState).isAccepted) {
                stateManage.add("\t\"" + managePath.fromState + " dep:" + managePath.fromDepth + "\" [shape = doublecircle];");
            } else if (fa.containsKey(managePath.fromState)) {
                stateManage.add("\t\"" + managePath.fromState + " dep:" + managePath.fromDepth + "\";");
            }
            if (managePath.readchar < comp.length) {
                if (comp[managePath.readchar].equals("EPS")) {
                    transManage.add("\t\"" + (managePath.fromState + " dep:" + (managePath.fromDepth)) + "\" -> \"" + managePath.nowState + " dep:" + managePath.nowDepth + "\" [label = \"ε\"];");
                } else {
                    transManage.add("\t\"" + (managePath.fromState + " dep:" + (managePath.fromDepth)) + "\" -> \"" + managePath.nowState + " dep:" + managePath.nowDepth + "\" [label = \"" + comp[managePath.readchar] + "\"];");
                }
            }
            if (fa.get(managePath.nowState).isAccepted) {
                ans = true;
            }
        }
        for (String ss : stateManage) {
            lp(ss);
        }
        lp("");
        for (String ss : transManage) {
            lp(ss);
        }
        lp("}");
        return ans;
    }

    private static class Path {
        int readchar;       //計算している文字位置
        int fromDepth;      //1つ前の深さ
        int nowDepth;       //今の深さ
        String fromState;       //1つ前のstate
        String nowState;    //現在居るstate

        Path(int readchar, int fromDepth, int nowDepth, String fromState, String nowState) {
            this.readchar = readchar;
            this.fromDepth = fromDepth;
            this.nowDepth = nowDepth;
            this.fromState = fromState;
            this.nowState = nowState;
        }
    }

    static void lp(Object o) {
        System.out.println(o);
    }
}

