/*
// $Id$
// This software is subject to the terms of the Common Public License
// Agreement, available at the following URL:
// http://www.opensource.org/licenses/cpl.html.
// Copyright (C) 2006-2006 Julian Hyde
// All Rights Reserved.
// You must accept the terms of that agreement to use this software.
*/
package mondrian.olap.fun;

import mondrian.olap.FunDef;
import mondrian.olap.Evaluator;
import mondrian.olap.Dimension;
import mondrian.calc.Calc;
import mondrian.calc.ExpCompiler;
import mondrian.calc.ListCalc;
import mondrian.calc.impl.ValueCalc;
import mondrian.calc.impl.AbstractDoubleCalc;
import mondrian.mdx.ResolvedFunCall;

import java.util.List;

/**
 * Definition of the <code>Sum</code> MDX function.
 *
 * @author jhyde
 * @version $Id$
 * @since Mar 23, 2006
 */
class SumFunDef extends AbstractAggregateFunDef {
    static final ReflectiveMultiResolver Resolver = new ReflectiveMultiResolver(
            "Sum",
            "Sum(<Set>[, <Numeric Expression>])",
            "Returns the sum of a numeric expression evaluated over a set.",
            new String[]{"fnx", "fnxn"},
            SumFunDef.class);

    public SumFunDef(FunDef dummyFunDef) {
        super(dummyFunDef);
    }

    public Calc compileCall(ResolvedFunCall call, ExpCompiler compiler) {
        final ListCalc listCalc =
                compiler.compileList(call.getArg(0));
        final Calc calc = call.getArgCount() > 1 ?
                compiler.compileScalar(call.getArg(1), true) :
                new ValueCalc(call);
        return new AbstractDoubleCalc(call, new Calc[] {listCalc, calc}) {
            public double evaluateDouble(Evaluator evaluator) {
                List memberList = listCalc.evaluateList(evaluator);
                return sumDouble(evaluator.push(), memberList, calc);
            }

            public Calc[] getCalcs() {
                return new Calc[] {listCalc, calc};
            }

            public boolean dependsOn(Dimension dimension) {
                return anyDependsButFirst(getCalcs(), dimension);
            }
        };
    }
}

// End SumFunDef.java