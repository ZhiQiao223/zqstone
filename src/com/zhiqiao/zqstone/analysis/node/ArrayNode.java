package com.zhiqiao.zqstone.analysis.node;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import com.zhiqiao.zqstone.analysis.AnalysisNode;
import com.zhiqiao.zqstone.analysis.TerminalSymbol;


public class ArrayNode extends AnalysisNode {

    public List<ExpressionNode> content = new ArrayList<>();

    @Override
    public void print(int retractNum, PrintStream out) {
        out.print("[");
        printParams(retractNum, out, content);
        out.print("]");
    }

    @Override
    public void match(AnalysisNode analysisNode) { }

    @Override
    public void match(TerminalSymbol token) { }
}
