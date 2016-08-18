package com.zhiqiao.zqstone.analysis.node;

import java.io.PrintStream;

import com.zhiqiao.zqstone.analysis.AnalysisNode;


public abstract class WhenNode extends AnalysisNode {

    public ExpressionNode condition;

    @Override
    public void print(int retractNum, PrintStream out) {
        out.print(" when ");
        condition.print(retractNum, out);
    }
}
