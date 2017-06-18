x:
			.word	1
y:
			.word	2
z:
			.word	4
f:
			.word	3
.S0:
			.string	"test"
main:
			PUSH	%BP
			MOV	%SP,%BP
@main_body:
			SUB	%SP,$4,%SP
			MOV	$.S0,-4(%BP)
			ADD	y,f,%0
			MOV	%0,%13
			JMP	@main_exit
@main_exit:
			MOV	%BP,%SP
			POP	%BP
			RET
