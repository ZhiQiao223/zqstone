package com.zhiqiao.zqstone.analysis.node;

import java.io.PrintStream;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.zhiqiao.zqstone.analysis.AnalysisNode;
import com.zhiqiao.zqstone.analysis.TerminalSymbol;

public class ContainerNode extends AnalysisNode {

    public Map<String, ExpressionNode> content = new LinkedHashMap<>();

    @Override
    public void print(int retractNum, PrintStream out) {
        Iterator<Entry<String, ExpressionNode>> it = content.entrySet().iterator();
        out.print("{");
        while(it.hasNext()) {
            Entry<String, ExpressionNode> e = it.next();
            out.print(e.getKey());
            out.print(" : ");
            e.getValue().print(retractNum, out);
            if(it.hasNext()) {
                out.print(", ");
            }
        }
        out.print("}");
    }

    @Override
    public void match(AnalysisNode analysisNode) {}

    @Override
    public void match(TerminalSymbol token) {}
}
