#ifndef __SIMPLE_AST_PRINT_NODE_H__
#define __SIMPLE_AST_PRINT_NODE_H__

#include <cdk/ast/sequence_node.h>

namespace til {

  /**
   * Class for describing print nodes.
   */
  class print_node : public cdk::basic_node {
    cdk::sequence_node *_argument;
    bool _append_newline = false;

  public:
    print_node(int lineno, cdk::sequence_node *argument, bool append_newline = false):
        cdk::basic_node(lineno), _argument(argument), _append_newline(append_newline){}

    cdk::sequence_node *argument() { return _argument; }

    bool append_newline() { return _append_newline; }

    void accept(basic_ast_visitor *sp, int level) { sp->do_print_node(this, level); }

  };

} // til

#endif
