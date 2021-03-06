package com.zhiqiao.zqstone.analysis;

import java.io.IOException;
import java.util.HashSet;

import com.zhiqiao.zqstone.analysis.NonTerminalSymbol.Exp;
import com.zhiqiao.zqstone.analysis.Token.Type;
import com.zhiqiao.zqstone.analysis.node.ChunkNode;
import com.zhiqiao.zqstone.analysis.node.StartChunkNode;


public class SyntacticAnalysis {

    private final LexicalAnalysis lexicalAnalysis;

    public SyntacticAnalysis(LexicalAnalysis lexicalAnalysis) {
        this.lexicalAnalysis = lexicalAnalysis;
    }

    TerminalSymbol readBuffered = null;
    TerminalSymbol lastRead = null;

    private TerminalSymbol read() throws IOException, LexicalAnalysisException {
        TerminalSymbol symbol;
        if(readBuffered == null) {
            Token token;
            do {
                token = lexicalAnalysis.read();
            } while(token.type == Type.Annotation);

            symbol = new TerminalSymbol(token.type, token.value);
        } else{
            symbol = readBuffered;
            readBuffered = null;
        }
        lastRead = symbol;
        return symbol;
    }

    private void back() {
        if(readBuffered != null) {
            throw new RuntimeException();
        }
        readBuffered = lastRead;
    }

    public ChunkNode analyze() throws IOException, LexicalAnalysisException, SyntacticAnalysisException {
        NonTerminalSymbol node = SyntacticDefine.getNonTerminalSymbol(Exp.StartChunk);
        AnalysisNode container = AnalysisDefine.createContainer(Exp.StartChunk);
        TerminalSymbol token = read();
        boolean matches = analyzeConsiderSign(container, node, token);
        if(!matches) {
            throw new SyntacticAnalysisException();
        }
        StartChunkNode startChunk = (StartChunkNode) container;
        return startChunk.chunk;
    }

    private boolean analyzeConsiderSign(AnalysisNode container, NonTerminalSymbol node, TerminalSymbol token) throws IOException, LexicalAnalysisException, SyntacticAnalysisException {
        boolean many, empty;
        if(node.sign == null) {
            many = false;
            empty = false;
        } else if(node.sign == '?') {
            many = false;
            empty = true;
        } else if(node.sign == '+') {
            many = true;
            empty = false;
        } else if(node.sign == '*'){
            many = true;
            empty = true;
        } else {
            throw new RuntimeException("unknown sign '"+ node.sign + "'");
        }
        boolean notGenerateEmpty = analyzeWith(container, node, token);

        if(!empty && !notGenerateEmpty) {
            unexpect(token, node);
        }

        if(many && notGenerateEmpty) {
            while(notGenerateEmpty) {
                token = read();
                notGenerateEmpty = analyzeWith(container, node, token);
            }
            back();
            notGenerateEmpty = true;
        }
        return notGenerateEmpty;
    }

    private boolean analyzeWith(AnalysisNode container, NonTerminalSymbol node, TerminalSymbol token) throws IOException, LexicalAnalysisException, SyntacticAnalysisException {
        boolean notGenerateEmpty;
        int index = selectExpansionIndex(node, token);
        if(index != -1) {
            Object[] expansion = node.expansionList.get(index);
            matchesExpansion(container, node, expansion, token);
            notGenerateEmpty = true;
        } else {
            notGenerateEmpty = false;
        }
        return notGenerateEmpty;
    }

    private int selectExpansionIndex(NonTerminalSymbol node, TerminalSymbol token) {
        int length = node.firstSetList.size();
        int index = -1;
        for(int i = 0; i<length; ++i) {
            HashSet<TerminalSymbol> firstSet = node.firstSetList.get(i);
            if(firstSet.contains(token)) {
                index = i;
                break;
            }
        }
        return index;
    }

    private void matchesExpansion(AnalysisNode container, NonTerminalSymbol parentNode, Object[] expansion, TerminalSymbol firstToken) throws IOException, LexicalAnalysisException, SyntacticAnalysisException {
        TerminalSymbol token = firstToken;
        boolean notGenerateAllEmpty = false;
        for(Object obj:expansion) {
            boolean notGenerateEmpty;
            NonTerminalSymbol node = tryGetNonTerminalSymbol(obj);
            if(node != null) {
                notGenerateEmpty = analyzeConsiderContainer(container, node, token);
            } else {
                TerminalSymbol needToken = (TerminalSymbol) obj;
                if(!needToken.equals(token)) {
                    unexpect(token, needToken);
                }
                container.match(token);
                notGenerateEmpty = true;
            }
            if(notGenerateEmpty) {
                token = read();
            }
            notGenerateAllEmpty |= notGenerateEmpty;
        }
        back();
        if (!notGenerateAllEmpty) {
            unexpect(token, parentNode);
        }
    }

    private boolean analyzeConsiderContainer(AnalysisNode container, NonTerminalSymbol node, TerminalSymbol token) throws IOException, LexicalAnalysisException, SyntacticAnalysisException {
        boolean notGenerateEmpty;
        AnalysisNode childContainer = null;
        if(container.getExp() != node.exp) {
            childContainer = AnalysisDefine.createContainer(node.exp);
        }
        if(childContainer == null) {
            notGenerateEmpty = analyzeConsiderSign(container, node, token);
        } else {
            notGenerateEmpty = analyzeConsiderSign(childContainer, node, token);
            if(notGenerateEmpty) {
                container.match(childContainer);
            }
            childContainer.finish();
        }
        return notGenerateEmpty;
    }

    private NonTerminalSymbol tryGetNonTerminalSymbol(Object obj) {
        NonTerminalSymbol node = null;
        if(obj instanceof NonTerminalSymbol) {
            node = (NonTerminalSymbol) obj;

        } else if(obj instanceof Exp){
            Exp exp = (Exp) obj;
            node = SyntacticDefine.getNonTerminalSymbol(exp);
        }
        return node;
    }

    private void unexpect(TerminalSymbol token, Object wantobj) throws SyntacticAnalysisException {
        if(token.type == Type.EndSymbol) {
            throw new SyntacticAnalysisException("unexpect $END wanted " + wantobj);
        } else {
            throw new SyntacticAnalysisException("unexpect " + token.value + " wanted " + wantobj);
        }
    }
}
