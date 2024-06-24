import {
  Button,
  Modal as ChakraModal,
  ModalBody,
  ModalCloseButton,
  ModalContent,
  ModalFooter,
  ModalHeader,
  ModalOverlay,
} from '@chakra-ui/react'

interface Props {
  isOpen: boolean
  onClose: () => void
  title: string
  cancelMessage: string
  confirmMessage: string
  confirmCallback: () => void
  confirmButtonColorScheme?: string
  children?: React.ReactNode
}

const Modal = ({
  isOpen,
  onClose,
  title,
  cancelMessage,
  confirmMessage,
  confirmCallback,
  confirmButtonColorScheme = 'green',
  children,
}: Props) => {
  return (
    <ChakraModal isOpen={isOpen} onClose={onClose} isCentered size="sm">
      <ModalOverlay />
      <ModalContent>
        <ModalHeader fontSize="lg" fontWeight="bold">
          {title}
        </ModalHeader>
        <ModalCloseButton />
        <ModalBody>{children}</ModalBody>
        <ModalFooter>
          <Button colorScheme="gray" mr={3} onClick={onClose} size="sm">
            {cancelMessage}
          </Button>
          <Button
            variant="solid"
            colorScheme={confirmButtonColorScheme}
            size="sm"
            onClick={confirmCallback}
          >
            {confirmMessage}
          </Button>
        </ModalFooter>
      </ModalContent>
    </ChakraModal>
  )
}

export default Modal
