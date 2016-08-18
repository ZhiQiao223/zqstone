package com.zhiqiao.zqstone.analysis.node;

import java.io.PrintStream;

import com.zhiqiao.zqstone.analysis.AnalysisNode;


public abstract class DefineVariableNode extends AnalysisNode {

    public String variableName;
    public ExpressionNode initValue = null;

    @Override
    public void print(int retractNum, PrintStream out) {
        out.print("var ");
        out.print(variableName);
        if(initValue != null) {
            out.print(" = ");
            initValue.print(retractNum, out);
        }
    }

}
