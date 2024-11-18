#ifndef __CDK19_AST_AND_NODE_H__
#define __CDK19_AST_AND_NODE_H__

#include <cdk/ast/binary_operation_node.h>

namespace cdk {

  /**
   * Class for describing the and operator
   */
  class and_node : public binary_operation_node {
  public:
    and_node(int lineno, expression_node *left, expression_node *right) noexcept:
        binary_operation_node(lineno, left, right) {
    }

    void accept(basic_ast_visitor *sp, int level) override { sp->do_and_node(this, level); }

  };

} // cdk

#endif