package com.gluonhq.vworkflows.lang;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.gluonhq.vworkflows.views.helper.DoubleValue;
import com.gluonhq.vworkflows.views.helper.FunctionValue;
import com.gluonhq.vworkflows.views.helper.OperatorValue;

import eu.mihosoft.vrl.workflow.Connector;
import eu.mihosoft.vrl.workflow.VFlow;
import eu.mihosoft.vrl.workflow.VNode;

/**
 * <p>This class compiles a specified flow to a mathematical expression that is compatible with
 * Java and Groovy. That is, it can be interpreted and evaluated via the groovy shell and/or JShell. 
 * </p>
 * <p>
 * <b>NOTE:</b>The flow has to use "data" flows exclusively. Currently, only the value objects in the
 * {@link com.gluonhq.vworkflows.views.helper} package are supported.
 * </p>
 * 
 * @author Michael Hoffer (info@michaelhoffer.de)
 */
public final class FlowToJExprCompiler {

    public static class Result {

        private final String expression;
        private final boolean valid;

        public Result(String expression, boolean valid) {
            this.expression = expression;
            this.valid = valid;
        }
        
        public String getExpression() {
            return expression;
        }

        public boolean isValid() {
            return valid;
        }

        @Override
        public String toString() {
            return "[expr: " + getExpression() + ", valid: "+isValid()+"]";
        }

    }

    /**
     * Predicate that determines whether the specified connector is not connected.
     */
    private static Predicate<Connector> notConnected = (c) -> {
        return c.getNode().getFlow().getConnections(c.getType()).getAllWith(c).isEmpty();
    };

    /**
     * Predicate that determines whether the specified connector is connected.
     */
    private static Predicate<Connector> connected = (c) -> {
        return !c.getNode().getFlow().getConnections(c.getType()).getAllWith(c).isEmpty();
    };

    /**
     * Predicate that determines whether the specified node is a terminal symbol.
     */
    private static Predicate<VNode> isTerminal = (n) -> {
        return n.getInputs().size() == 0;
    };

    /**
     * Predicate that determines whether the specified node is a return value.
     */
    private static Predicate<VNode> isReturnValue = (n) -> {
        return n.getOutputs().stream().filter(connected).count() == 0;
    };

    /**
     * Compiles the specified flow to an expression string, e.g., {@code (2+3)*(3-2)}.
     * @param f flow to compile
     * @return compiled flow as Java/Groovy compatible expression
     */
    public Result compile(VFlow f) {

        List<VNode> retVal = f.getNodes().filtered(isReturnValue);

        if (retVal.size() != 1) {
            // TODO do we prefer exceptions?
            return new Result("ERROR: wrong number of return values. Got '" + retVal.size() + "', expected 1.",false);
        }

        List<VNode> terminals = f.getNodes().filtered(isTerminal);

        VNode root = retVal.get(0);

        return nodeToString(root);
        
    }

    Result nodeToString(VNode n) {
        if(n.getValueObject().getValue()==null) {
            return new Result("/*null*/", false);
        } else

        if(n.getValueObject().getValue() instanceof DoubleValue) {
            DoubleValue v = (DoubleValue) n.getValueObject().getValue();
            return new Result(""+v.getValue(), true);
        } else

        if(n.getValueObject().getValue() instanceof FunctionValue) {
            FunctionValue v = (FunctionValue) n.getValueObject().getValue();

            // TODO ensure exactly one connected object

            Result res = nodeToString(getInputs(n, "data").get(0));

            switch(v.getValue()) {
                case SINE:   return new Result("Math.sin(" + res.getExpression()+ ")", res.isValid());
                case COSINE: return new Result("Math.cos(" + res.getExpression()+ ")", res.isValid());
                // TODO do we prefer exceptions?
                default:     return new Result("/*unsupported*/", false);            
            }
        } else 

        if(n.getValueObject().getValue() instanceof OperatorValue) {
            OperatorValue v = (OperatorValue) n.getValueObject().getValue();

            // TODO ensure exactly two connected object

            List<VNode> inputs = getInputs(n, "data");

            if(inputs.size() < 2) {
                return new Result("ERROR: wrong number of connections. Got " + inputs.size() + ", expected 2.", false);     
            }

            Result resL = nodeToString(inputs.get(0));
            Result resR = nodeToString(inputs.get(1));

            switch(v.getValue()) {
                case ADD:      return new Result("("+resL.getExpression() + " + " + resR.getExpression()+")", resL.isValid() && resR.isValid());
                case SUBTRACT: return new Result("("+resL.getExpression() + " - " + resR.getExpression()+")", resL.isValid() && resR.isValid());                        
                case MULTIPLY: return new Result("("+resL.getExpression() + " * " + resR.getExpression()+")", resL.isValid() && resR.isValid());
                case DIVIDE:   return new Result("("+resL.getExpression() + " / " + resR.getExpression()+")", resL.isValid() && resR.isValid());                        
                            
                default: 
                               // TODO do we prefer exceptions?
                               return new Result("/*unsupported*/", false);         
            }
        }
        
        // TODO do we prefer exceptions?
        return new Result("/*unsupported*/", false);   
    }

    /**
     * <p>
     * Returns all nodes that are connected to the inputs of the specified node.
     * </p>
     * <p>
     * <b>NOTE:</b> only the specified connection type is analyzed. Other connections
     * are ignored.
     * </p>
     * @param n node to analyze
     * @param type connection type, e.g., "data"
     * @return list of all nodes that are connected to the inputs of the specified node
     */
    private List<VNode> getInputs(VNode n, String type) {
        List<VNode> result = new ArrayList<>();
        for(Connector c : n.getInputs()) {
            List<VNode> inputs = n.getFlow().getAllConnections().get(type).getAllWith(c).stream().
              map(conn->conn.getSender().getNode()).collect(Collectors.toList());
            result.addAll(inputs);  
        }

        return result;
    }
    
}
