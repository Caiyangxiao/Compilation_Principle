import java.io.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

public class Classification {

    public static void main(String[] args) throws IOException {
        //File file = new File("grammar_type0.txt");
        File file = new File("grammar_type1.txt");
        //File file = new File("grammar_type2.txt");
        //File file = new File("grammar_type3.txt");

        FileInputStream fileInputStream = new FileInputStream(file);
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fileInputStream));
        //读取非终结符
        String VN = bufferedReader.readLine();
        String []VNs = VN.split(",");
        HashSet<String> SetVNs = new HashSet<String>(Arrays.asList(VNs));
        //读取终结符
        String VT = bufferedReader.readLine();
        String []VTs = VT.split(",");
        HashSet<String> SetVTs = new HashSet<String>(Arrays.asList(VTs));
        //读取生成式
        String P = bufferedReader.readLine();
        String []Ps = P.split(",");
        //读取开始符
        String S = bufferedReader.readLine();

        //HashMap存储产生式对应关系
        HashMap<String,String> map = new HashMap<>();
        for(int i = 0;i<Ps.length;i++){
            String[] split = Ps[i].split("->");
            //产生式左部
            String left = split[0];
            //产生式右部
            String right = split[1];
            map.put(left,right);

        }
        int type = 4;
        for (String left:map.keySet()) {
            String right = map.get(left);
            if(!judgeValid(left,right,SetVNs,SetVTs)){
                System.out.println("输入的产生式不合法，请重新输入");
                return;
            }
            //判断是否为3型
            if(judgeThreeGrammar(left,right,SetVNs,SetVTs,map)){
                type = Math.min(type,3);
            }
            else if(judgeTwoGrammar(left,right,SetVNs,SetVTs)){
                type = Math.min(type,2);
            }
            else if(judgeOneGrammar(left,right,SetVNs)){
                type = Math.min(type,1);
            }
            else if(judgeZeroGrammar(left,SetVNs)){
                type = Math.min(type,0);
            }
            //不是文法
            else{
                type = -1;
            }
        }

        System.out.print("非终结符有：");
        for (String VNout:
        SetVNs) {
            System.out.print(VNout+" ");
        }
        System.out.println();

        System.out.print("终结符有：");
        for (String VTout:
                SetVTs) {
            System.out.print(VTout+" ");
        }
        System.out.println();

        System.out.print("产生式有：");
        for (String Pout:
                Ps) {
            System.out.print(Pout+" ");
        }
        System.out.println();

        System.out.println("开始符为："+S);


        switch (type){
            case 3:
                System.out.println("该文法是3型文法");
                break;
            case 2:
                System.out.println("该文法是2型文法");
                break;
            case 1:
                System.out.println("该文法是1型文法");
                break;
            case 0:
                System.out.println("该文法是0型文法");
                break;
            case -1:
                System.out.println("该文法不是文法");
                break;
        }

        //关闭IO流
        bufferedReader.close();
        fileInputStream.close();


    }

    //  判断该非终结符对应的生成式是右终结符（1）还是左终结符（2）,都符合(0),都不符合(-1)
    private static int JudgeVNLinear(String VN,HashSet<String> SetVNs,HashSet<String> SetVTs,HashMap<String,String> map){
        //先判断是否为非终结符
        if(SetVNs.contains(VN)){
            //判断产生式是否为3型文法
            String result = map.get(VN);
            //式子右边两个字符
            if(result.length()==2){
                //判断是左线型还是右线型
                String first = result.substring(0,1);
                String second = result.substring(1);
                //左边为非终结符，右边为终结符,判断是否为左线性
                if(SetVNs.contains(first)&&SetVTs.contains(second)){
                    if(first.equals(VN)||JudgeVNLinear(first,SetVNs,SetVTs,map)==2)
                        return 2;
                }
                //左边为终结符，右边为非终结符，判断是否为右线性
                else if(SetVNs.contains(second)&&SetVTs.contains(first)){
                    if(second.equals(VN)||JudgeVNLinear(second,SetVNs,SetVTs,map)==1)
                        return 1;
                }
            }
            //右边只有一个终结符
            else if(result.length()==1){
                if(SetVTs.contains(result))
                    return 0;
            }
        }
        return -1;
    }

    //判断是否为3型文法
    private static boolean judgeThreeGrammar(String left,String right,HashSet<String> SetVNs,HashSet<String> SetVTs,HashMap<String,String> map){
        //先判断左边，左边只能有一个字符，而且必须是非终结符
        if(left.length()==1&&SetVNs.contains(left)){
            //判别式的右边最多只能有两个字符，且当判别式的右边有两个字符时必须有一个为终结符而另一个为非终结符。
            if(right.length()==2){
                //判断是左线型还是右线型
                String first = right.substring(0,1);
                String second = right.substring(1);
                //左边为非终结符(对应产生式也为左线性)，右边为终结符,判断是为左线性
                if(SetVNs.contains(first)&&SetVTs.contains(second)){
                    int flag = JudgeVNLinear(first,SetVNs,SetVTs,map);
                    if((flag==0||flag==2)&&flag!=1)
                        return true;
                }
                //左边为终结符，右边为非终结符(对应产生式也为右线性)，判断是否为右线性
                else if(SetVNs.contains(second)&&SetVTs.contains(first)){
                    int flag = JudgeVNLinear(second,SetVNs,SetVTs,map);
                    if((flag==0||flag==1)&&flag!=2)
                        return true;
                }

            }
            //当判别式的右边只有一个字符时，此字符必须为终结符；
            else if(right.length()==1){
                if(SetVTs.contains(right))
                    return true;
            }

        }
        return false;

    }

    //判断是否为2型文法
    private static boolean judgeTwoGrammar(String left,String right,HashSet<String> SetVNs,HashSet<String> SetVTs){
        //左边必须有且仅有一个非终结符
        if(left.length()==1&&SetVNs.contains(left)){
            return true;

        }
        return false;

    }

    //判断是否为1型文法
    private static boolean judgeOneGrammar(String left,String right,HashSet<String> SetVNs){
        if(right.length()>=left.length()){
            //左边必须有一个非终结符
            for(int i = 0;i<left.length();i++){
                String s = (String.valueOf(left.charAt(i)));
                if(SetVNs.contains(s))
                    return true;
            }
        }
        //其实已经包含在上面的条件中
        else if(left.length()==1&&right.equals("ε")&&SetVNs.contains(left)){
            return true;
        }
        return false;
    }
    private static boolean judgeZeroGrammar(String left,HashSet<String> SetVNs){

        //左边必须有一个非终结符
        for(int i = 0;i<left.length();i++){
            String s = (String.valueOf(left.charAt(i)));
            if(SetVNs.contains(s))
                return  true;

        }

        return false;

    }

    //判断产生式是否合法(非终结符、终结符中是否存在）
    private static boolean judgeValid(String left,String right,HashSet<String> SetVNs,HashSet<String> SetVTs){
        //左边必须有一个非终结符
        for(int i = 0;i<left.length();i++){
            String s = (String.valueOf(left.charAt(i)));
            //如果终结符和非终结符中都不包含该字符，返回false
            if(!SetVNs.contains(s)&&!SetVTs.contains(s)&&!s.equalsIgnoreCase("ε"))
                return false;
        }
        for(int i = 0;i<right.length();i++){
            String s = (String.valueOf(right.charAt(i)));
            //如果终结符和非终结符中都不包含该字符，返回false
            if(!SetVNs.contains(s)&&!SetVTs.contains(s)&&!s.equalsIgnoreCase("ε"))
                return false;
        }
        return true;
    }
}