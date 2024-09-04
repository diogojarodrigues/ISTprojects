#ifndef __TIL_TARGETS_FRAME_CALCULATOR_H__
#define __TIL_TARGETS_FRAME_CALCULATOR_H__

#include "targets/basic_ast_visitor.h"

namespace til {
    
    class frame_calculator: public basic_ast_visitor {
        cdk::symbol_table<til::symbol> &_symtab;
        size_t _localsize;

        public:
            frame_calculator(std::shared_ptr<cdk::compiler> compiler, cdk::symbol_table<til::symbol> &symtab) :
                basic_ast_visitor(compiler), _symtab(symtab), _localsize(0) {
            }

        public:
            ~frame_calculator() {
                os().flush();
            }	
        public:
            inline size_t localsize() {
                return _localsize;
            }
        public:
// do not edit these lines
#define __IN_VISITOR_HEADER__
#include ".auto/visitor_decls.h"       // automatically generated
#undef __IN_VISITOR_HEADER__
// do not edit these lines: end
    };
}
#endif