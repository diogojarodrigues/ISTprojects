#ifndef __CDK19_AST_MUL_NODE_H__
#define __CDK19_AST_MUL_NODE_H__

#include <cdk/ast/binary_operation_node.h>

namespace cdk {

  /**
   * Class for describing the multiplication operator
   */
  class mul_node : public binary_operation_node {
  public:
    mul_node(int lineno, expression_node *left, expression_node *right) noexcept:
        binary_operation_node(lineno, left, right) {
    }

    void accept(basic_ast_visitor *sp, int level) override { sp->do_mul_node(this, level); }

  };

} // cdk

#endif
