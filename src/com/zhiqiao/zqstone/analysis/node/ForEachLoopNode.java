package com.zhiqiao.zqstone.analysis.node;

import java.io.PrintStream;
import java.util.List;

import com.zhiqiao.zqstone.analysis.AnalysisNode;

public abstract class ForEachLoopNode extends AnalysisNode {

    public List<AnalysisNode> beforeCommandList = null;
    public List<AnalysisNode> afterCommandList = null;
    public ExpressionNode loopCondition;
    public ChunkNode chunk;

    @Override
    public void print(int retractNum, PrintStream out) {
        out.print("for (");
        printParams(retractNum, out, beforeCommandList);
        out.print("; ");
        loopCondition.print(retractNum, out);
        out.print("; ");
        printParams(retractNum, out, afterCommandList);
        out.print(") do\n");
        chunk.print(retractNum + 1, out);
        printRetract(retractNum, out);
        out.print("end");
    }
}
